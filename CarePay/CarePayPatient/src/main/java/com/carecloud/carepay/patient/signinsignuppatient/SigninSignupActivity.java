package com.carecloud.carepay.patient.signinsignuppatient;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.MenuItem;

import com.carecloud.carepay.patient.base.BasePatientActivity;
import com.carecloud.carepay.patient.signinsignuppatient.fragments.SigninFragment;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInLablesDTO;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInSignUpDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

/**
 * Created by harish_revuri on 9/7/2016.
 * Activity supporting Signin and Sign-up
 */
public class SigninSignupActivity extends BasePatientActivity {


    private SignInSignUpDTO signInSignUpDTO;
    private SignInLablesDTO signInLablesDTO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        signInSignUpDTO = getConvertedDTO(SignInSignUpDTO.class);
        signInLablesDTO=signInSignUpDTO.getMetadata().getLabels();
        if (signInSignUpDTO != null && !HttpConstants.isUseUnifiedAuth()) {
            getApplicationMode().setCognitoDTO(signInSignUpDTO.getPayload().getPatientAppSignin().getCognito());
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.layoutSigninSignup, new SigninFragment(), SigninFragment.class.getSimpleName())
                    .commit();
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

    public SignInSignUpDTO getSignInSignUpDTO() {
        return signInSignUpDTO;
    }

    public void setSignInSignUpDTO(SignInSignUpDTO signInSignUpDTO) {
        this.signInSignUpDTO = signInSignUpDTO;
    }

    public SignInLablesDTO getSignInLablesDTO() {
        return signInLablesDTO;
    }

    public void setSignInLablesDTO(SignInLablesDTO signInLablesDTO) {
        this.signInLablesDTO = signInLablesDTO;
    }
}