package com.carecloud.carepay.patient.signinsignuppatient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.signinsignuppatient.fragments.SigninFragment;
import com.carecloud.carepay.patient.tutorial.tutorial.TutorialActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by harish_revuri on 9/7/2016.
 * Activity supporting Signin and Sign-up
 */
public class SigninSignupActivity extends BasePatientActivity implements FragmentActivityInterface {

    private SignInDTO signInSignUpDTO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        signInSignUpDTO = getConvertedDTO(SignInDTO.class);
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
}