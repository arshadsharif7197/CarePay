package com.carecloud.carepaylibray.signinsignup;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInLablesDTO;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInSignUpDTO;
import com.carecloud.carepaylibray.signinsignup.fragments.SigninFragment;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by harish_revuri on 9/7/2016.
 * Activity supporting Signin and Sign-up
 */
public class SigninSignupActivity extends AppCompatActivity {


    private SignInSignUpDTO signInSignUpDTO;
    private SignInLablesDTO signInLablesDTO;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.layoutSigninSignup, new SigninFragment(), SigninFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
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