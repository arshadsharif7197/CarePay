package com.carecloud.carepay.patient.signinsignuppatient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.signinsignuppatient.fragments.SigninFragment;
import com.carecloud.carepay.patient.tutorial.tutorial.TutorialActivity;
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
            fragmentManager.beginTransaction()
                    .replace(R.id.layoutSigninSignup, new SigninFragment(), SigninFragment.class.getSimpleName())
                    .commit();
        }
        if (!getApplicationPreferences().isTutorialShown()) {
            startActivity(new Intent(getContext(), TutorialActivity.class));
            getApplicationPreferences().setTutorialShown(true);
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
}