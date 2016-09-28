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
        replaceFragment(SelectLanguageFragment.class, false);
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
        // check if user logged in, if yes go to appointments
        if(!findCurrentUser()) {
            // else to SignIn/SignUp
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

    // Cognito
    private boolean findCurrentUser() {
        CognitoUser user = CognitoAppHelper.getPool().getCurrentUser();
        String userName = user.getUserId();
        if(userName != null) {
            CognitoAppHelper.setUser(userName);
            user.getSessionInBackground(authenticationHandler);
            return true;
        }
        return false;
    }

    private AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            CognitoAppHelper.setCurrSession(userSession);
            CognitoAppHelper.newDevice(newDevice);

            // move to Appointments
            Intent intent = new Intent(LibraryMainActivity.this, AppointmentsActivity.class);
            startActivity(intent);
            LibraryMainActivity.this.finish();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String UserId) {
            Log.v(LOG_TAG, "getAuthenticationDetails()");
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation continuation) {
            Log.v(LOG_TAG, "getMFACode()");
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            Log.v(LOG_TAG, "authenticationChallenge()");
        }

        @Override
        public void onFailure(Exception exception) {
            Log.e(LOG_TAG, exception.getLocalizedMessage());
        }
    };
}
