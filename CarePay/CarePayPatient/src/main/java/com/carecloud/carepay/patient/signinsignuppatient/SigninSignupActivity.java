package com.carecloud.carepay.patient.signinsignuppatient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.patientsplash.SplashActivity;
import com.carecloud.carepay.patient.signinsignuppatient.fragments.SigninFragment;
import com.carecloud.carepay.patient.tutorial.tutorial.TutorialActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.BaseFragmentInterface;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.ResetPasswordViewModel;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by harish_revuri on 9/7/2016.
 * Activity supporting Signin and Sign-up
 */
public class SigninSignupActivity extends BasePatientActivity implements FragmentActivityInterface, BaseFragmentInterface,
        FingerPrintInterface {

    private SignInDTO signInSignUpDTO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        signInSignUpDTO = getConvertedDTO(SignInDTO.class);
        setUpViewModel();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            boolean shouldOpenNotifications = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO)
                    .getBoolean(CarePayConstants.OPEN_NOTIFICATIONS, false);
            SigninFragment fragment = SigninFragment.newInstance(shouldOpenNotifications);
            fragmentManager.beginTransaction()
                    .replace(R.id.layoutSigninSignup, fragment, SigninFragment.class.getName())
                    .commit();
        }
        if (!getApplicationPreferences().isTutorialShown()) {
            startActivity(new Intent(getContext(), TutorialActivity.class));
            getApplicationPreferences().setTutorialShown(true);
        }
        boolean crash = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO)
                .getBoolean(CarePayConstants.CRASH, false);
        if (crash) {
            Toast.makeText(this, Label.getLabel("crash_handled_error_message"), Toast.LENGTH_LONG).show();
        }
    }

    private void setUpViewModel() {
        SignInViewModel viewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        setBasicObservers(viewModel);
        ResetPasswordViewModel resetPasswordViewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        resetPasswordViewModel.setResetPasswordTransition(signInSignUpDTO.getMetadata().getTransitions().getForgotPassword());
        setBasicObservers(resetPasswordViewModel);
        viewModel.setSignInDto(signInSignUpDTO);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getApplicationPreferences().mustForceUpdate()) {
            ConfirmDialogFragment fragment = ConfirmDialogFragment
                    .newInstance(Label.getLabel("notifications.custom.forceUpdate.title"),
                            Label.getLabel("notifications.custom.forceUpdate.message.android"),
                            Label.getLabel("notifications.custom.forceUpdate.action"),
                            false,
                            R.layout.fragment_alert_dialog_single_action);
            fragment.setCallback(() -> {
                String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            });
            displayDialogFragment(fragment, false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(SigninSignupActivity.this);
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public DTO getDto() {
        return signInSignUpDTO;
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.layoutSigninSignup, fragment, addToBackStack);
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.layoutSigninSignup, fragment, addToBackStack);
    }

    @Override
    public void startBaseFragment() {
        String name = getSupportFragmentManager().getBackStackEntryAt(0).getName();
        getSupportFragmentManager().popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void authenticate(String user, String pwd) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.layoutSigninSignup);
        if (fragment instanceof SigninFragment) {
            ((SigninFragment) fragment).signIn(user, pwd);
        }
    }

    @Override
    public boolean manageSession() {
        return false;
    }
}