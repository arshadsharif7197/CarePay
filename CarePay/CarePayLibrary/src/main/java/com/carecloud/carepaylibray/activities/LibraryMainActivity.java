package com.carecloud.carepaylibray.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.cognito.CognitoActionCallback;
import com.carecloud.carepaylibray.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.payment.ResponsibilityFragment;
import com.carecloud.carepaylibray.selectlanguage.fragments.SelectLanguageFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SigninFragment;
import com.carecloud.carepaylibray.signinsignup.fragments.SignupFragment;

public class LibraryMainActivity extends KeyboardHolderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // init Cognito
        CognitoAppHelper.init(getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void placeInitContentFragment() {
        // check if user logged in; if yes go to appointments
        boolean signedIn = CognitoAppHelper.findCurrentUser(new CognitoActionCallback() {
            @Override
            public void executeAction() {
                Intent intent = new Intent(LibraryMainActivity.this, AppointmentsActivity.class);
                startActivity(intent);
                LibraryMainActivity.this.finish();
            }
        });
        if (!signedIn) { // if not signed in, launch the flow
            replaceFragment(SelectLanguageFragment.class, false);
        } else {
            // show a splash screen
        }
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_keyboard_holder;
    }

    @Override
    public int getContentsHolderId() {
        return R.id.content_holder;
    }

    @Override
    public int getKeyboardHolderId() {
        return R.id.keyboard_holder;
    }

    /**
     * Replace the current content fragment with a new fragment specified by class
     *
     * @param fragClass The class
     */
    @Override
    public void replaceFragment(Class fragClass, boolean addToBackStack) {
        // else go to Signin
        Fragment fragment = fm.findFragmentByTag(fragClass.getSimpleName());
        if (fragment == null) {
            if (fragClass.equals(SelectLanguageFragment.class)) {
                fragment = new SelectLanguageFragment();
            } else if (fragClass.equals(SigninFragment.class)) {
                fragment = new SigninFragment();
            } else if (fragClass.equals(SignupFragment.class)) {
                fragment = new SignupFragment();
            } else if (fragClass.equals(ResponsibilityFragment.class)) {
                fragment = new ResponsibilityFragment();
            }
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(getContentsHolderId(), fragment, fragClass.getSimpleName());
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}
