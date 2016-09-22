package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.signinsignup.models.TextWatcherModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SigninFragment extends Fragment {

    private TextInputLayout emailTextInput, passwordTexInput;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView changeLanguageTextView, forgotPasswordTextView;
    private Button  signinButton;
    private Button  signupButton;
    private boolean isValidEmail, isValidPassword;

    private Typeface hintFontFamily;
    private Typeface editTextFontFamily;
    private Typeface floatingTextFontfamily;
    private Typeface buttonFontFamily;

    private String      userName;
    private ProgressBar progressBar;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.signInProgress);
        progressBar.setVisibility(View.INVISIBLE);

        emailTextInput = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        passwordTexInput = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);

        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);

        signinButton = (Button) view.findViewById(R.id.signin_button);
        signinButton.setEnabled(false);
        signupButton = (Button) view.findViewById(R.id.signup_button);

        // TODO: 9/14/2016 replace with SystemUtil.setTypeFace...
        hintFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");
        floatingTextFontfamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");
        buttonFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/gotham_rounded_medium.otf");

        emailEditText.setTypeface(editTextFontFamily);
        passwordEditText.setTypeface(editTextFontFamily);
        emailTextInput.setTypeface(hintFontFamily);
        passwordTexInput.setTypeface(hintFontFamily);
        signinButton.setTypeface(buttonFontFamily);
        signupButton.setTypeface(buttonFontFamily);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInfo()) {
                    signInUser();
                }
            }
        });

        signupButton = (Button) view.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                SignupFragment fragment = (SignupFragment) fm.findFragmentByTag(SignupFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new SignupFragment();
                }
                fm.beginTransaction()
                        .replace(R.id.layoutSigninSignup, fragment, SignupFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });

        changeLanguageTextView = (TextView) view.findViewById(R.id.changeLanguageText);
        forgotPasswordTextView = (TextView) view.findViewById(R.id.forgotPasswordTextView);
        changeLanguageTextView.setTypeface(editTextFontFamily);
        forgotPasswordTextView.setTypeface(editTextFontFamily);
        changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        setTextListeners();
        emailTextInput.setError(null);
        passwordTexInput.setError(null);

        return view;
    }

    /**
     * Set the focus change and selct text listeners for the edit texts
     */
    private void setTextListeners() {
        emailEditText.addTextChangedListener(
                new TextWatcherModel(editTextFontFamily,
                                     floatingTextFontfamily,
                                     TextWatcherModel.InputType.TYPE_EMAIL,
                                     emailEditText,
                                     emailTextInput,
                                     "Enter Valid Email",
                                     false,
                                     new TextWatcherModel.OnInputChangedListner() {
                                         @Override
                                         public void OnInputChangedListner(boolean isValid) {
                                             isValidEmail = isValid;
                                             checkForButtonEnable();
                                         }
                                     }));
        passwordEditText.addTextChangedListener(new TextWatcherModel(editTextFontFamily,
                                                                     floatingTextFontfamily,
                                                                     TextWatcherModel.InputType.TYPE_PASSWORD,
                                                                     passwordEditText, passwordTexInput,
                                                                     "Enter password", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidPassword = isValid;
                checkForButtonEnable();
            }
        }));

        // change hint to caps when floating hint
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String hint = getString(R.string.email_text);
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    emailTextInput.setHint(hintCaps);
                } else {
                    if (StringUtil.isNullOrEmpty(emailEditText.getText().toString())) {
                        // change hint to lower
                        emailTextInput.setHint(hint);

                    } else {
                        emailEditText.setHint(hint);
                    }
                }
            }
        });
        emailEditText.clearFocus();

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.v(LOG_TAG, "password has focus");
                String hint = getString(R.string.password_text);
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    passwordTexInput.setHint(hintCaps);
                } else {
                    if (StringUtil.isNullOrEmpty(passwordEditText.getText().toString())) {
                        passwordTexInput.setHint(hint);
                    } else {
                        // change hint to lower
                        passwordEditText.setHint(hintCaps);
                    }
                }
            }
        });
        passwordEditText.clearFocus();
    }

    private boolean isValidInfo() {

        if (isValidEmail && isValidPassword) {
            return true;
        } else if (!isValidEmail && !isValidPassword) {
            Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (!isValidEmail) {
            Toast.makeText(getActivity(), "Enter valid Email-ID", Toast.LENGTH_LONG).show();
            return false;
        } else if (!isValidPassword) {
            Toast.makeText(getActivity(), "Enter valid password", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void checkForButtonEnable() {
        if (isValidEmail && isValidPassword) {
            signinButton.setEnabled(true);
        } else {
            signinButton.setEnabled(false);
        }
    }

    // cognito
    private void signInUser() {
        Log.v(LOG_TAG, "sign in user");

        progressBar.setVisibility(View.VISIBLE);

        userName = emailEditText.getText().toString();
        CognitoAppHelper.setUser(userName);
        CognitoAppHelper.getPool().getUser(userName).getSessionInBackground(authenticationHandler);
    }

    private void launchUser() {
        Log.v(LOG_TAG, "launchUser()");

        Intent userActivity = new Intent(getActivity(), AppointmentsActivity.class);
        startActivity(userActivity);
        getActivity().finish();
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        Log.v(LOG_TAG, "getUserAuthentication()");

        userName = username;
        if (username != null) {
            CognitoAppHelper.setUser(username);
        }

        String password = passwordEditText.getText().toString();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    private AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.v(LOG_TAG, "Auth Success");

            CognitoAppHelper.setCurrSession(cognitoUserSession);
            CognitoAppHelper.newDevice(device);
            progressBar.setVisibility(View.INVISIBLE);
            launchUser();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.getDefault());
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
        }

        @Override
        public void onFailure(Exception e) {
            progressBar.setVisibility(View.INVISIBLE);
            SystemUtil.showDialogMessage(getActivity(),
                                         "Sign-in failed",
                                         "Invalid user id or password");// TODO: 9/21/2016 prepare for translation if kept
            Log.e(LOG_TAG, CognitoAppHelper.formatException(e));
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            // TODO change the place holder
        }
    };
}