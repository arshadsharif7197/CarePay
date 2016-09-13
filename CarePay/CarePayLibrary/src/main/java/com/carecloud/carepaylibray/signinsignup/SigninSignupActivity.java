package com.carecloud.carepaylibray.signinsignup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.signinsignup.fragments.SigninFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SignupFragment;

/**
 * Created by harish_revuri on 9/7/2016.
 */


public class SigninSignupActivity extends AppCompatActivity {

    private FragmentManager mFragmentManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);

        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            mFragmentManager.beginTransaction().add(R.id.layoutSigninSignup, new SigninFragment(), "signin").addToBackStack("signin").commit();
        }

        mFragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (mFragmentManager.getBackStackEntryCount() == 0) {
                    mFragmentManager.popBackStack();
                    finish();
                }
            }
        });

    }

    private void switchFragment(Fragment fragment, String tagName) {
        if (tagName != null) {
            mFragmentManager.beginTransaction()
                    .replace(R.id.layoutSigninSignup, fragment).addToBackStack(tagName).commit();
        }
    }

    public FragmentManager getmFragmentManager() {
        return mFragmentManager;
    }
}
