package com.carecloud.carepaylibray.signinsignup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.cognito.AppHelper;
import com.carecloud.carepaylibray.signinsignup.fragments.SigninFragment;

/**
 * Created by harish_revuri on 9/7/2016.
 */


public class SigninSignupActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);

        FragmentManager mFragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.layoutSigninSignup, new SigninFragment(), SigninFragment.class.getSimpleName())
                    .commit();
        }

//        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                if (mFragmentManager.getBackStackEntryCount() == 0) {
//                    mFragmentManager.popBackStack();
//                    finish();
//                }
//            }
//        });
    }
}
