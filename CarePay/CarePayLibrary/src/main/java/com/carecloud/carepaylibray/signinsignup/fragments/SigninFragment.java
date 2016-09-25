package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.carecloud.carepaylibrary.R;
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

    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTexInput;
    private EditText        emailEditText;
    private EditText        passwordEditText;
    private TextView        changeLanguageTextView;
    private TextView        forgotPasswordTextView;
    private Button          signinButton;
    private Button          signupButton;
    private ProgressBar     progressBar;
    private LinearLayout    parentLayout;

    private boolean isEmptyEmail;
    private boolean isEmptyPassword;
    private boolean isValidEmail;
    private boolean isValidPassword;

    private String userName;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        parentLayout = (LinearLayout) view.findViewById(R.id.signin_layout);

        progressBar = (ProgressBar) view.findViewById(R.id.signInProgress);
        progressBar.setVisibility(View.INVISIBLE);

        setEditTexts(view);

        setClickbles(view);

        setTypefaces();

        return view;
    }

    private void setClickbles(View view) {
        signinButton = (Button) view.findViewById(R.id.signin_button);
        signinButton.setEnabled(false);
        signupButton = (Button) view.findViewById(R.id.signup_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllValid()) {
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
                    fragment.setRetainInstance(true);
                }
                fm.beginTransaction()
                        .replace(R.id.layoutSigninSignup, fragment, SignupFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });

        changeLanguageTextView = (TextView) view.findViewById(R.id.changeLanguageText);
        forgotPasswordTextView = (TextView) view.findViewById(R.id.forgotPasswordTextView);

        changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setTypefaces() {
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), emailEditText);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), passwordEditText);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), emailTextInput);
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), passwordTexInput);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), signinButton);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), signupButton);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), changeLanguageTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), forgotPasswordTextView);

    }

    private void setEditTexts(View view) {
        emailTextInput = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        emailTextInput.setTag(getString(R.string.signin_signup_email_hint));
        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        emailEditText.setTag(emailTextInput);

        passwordTexInput = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);
        passwordTexInput.setTag(getString(R.string.signin_signup_password_hint));
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        passwordEditText.setTag(passwordTexInput);

        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();

        emailEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    /**
     * Set the focus change and selct text listeners for the edit texts
     */
    private void setTextListeners() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        emailEditText.clearFocus();
    }

    private void setChangeFocusListeners() {
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
//                    isValidEmail = checkEmail();
                } else {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
//                    isValidPassword = checkPassword();
//                    if (!isRepeatPasswordEmpty) {  // check reactively if the match password, if repeated not empty
//                        isPasswordMatch = checkPasswordsMatch();
//                    }
                } else {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    private void setActionListeners() {
        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
//                    isValidEmail = checkEmail();
                    passwordEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
//                    isValidPassword = checkPassword();
//                    if (!isRepeatPasswordEmpty) { // check reactively if the match password, if repeated not empty
//                        isPasswordMatch = checkPasswordsMatch();
                    passwordEditText.clearFocus();
                    parentLayout.requestFocus();

//                    }
//
//                    repeatPasswordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    private boolean checkEmail() {
        String email = emailEditText.getText().toString();
        isEmptyEmail = StringUtil.isNullOrEmpty(email);
        boolean isEmailValid = StringUtil.isValidmail(email);
        emailTextInput.setErrorEnabled(isEmptyEmail || !isEmailValid); // enable for error if either empty or invalid email
        if (isEmptyEmail) {
            emailTextInput.setError(getString(R.string.signin_signup_error_empty_email));
        } else if (!isEmailValid) {
            emailTextInput.setError(getString(R.string.signin_signup_error_invalid_email));
        } else {
            emailTextInput.setError(null);
        }
        return !isEmptyEmail && isEmailValid;
    }

    private boolean checkPassword() {
        isEmptyPassword = StringUtil.isNullOrEmpty(passwordEditText.getText().toString());
        String error = (isEmptyPassword ? getString(R.string.signin_signup_error_empty_password) : null);
        passwordTexInput.setErrorEnabled(isEmptyPassword);
        passwordTexInput.setError(error);
        return !isEmptyPassword;
    }

    private boolean areAllValid() {
        return isValidEmail && isValidPassword;
    }

    private void enableSignupButton() {
        boolean areAllNonEmpty = isEmptyEmail || isEmptyPassword;
        signinButton.setEnabled(areAllNonEmpty);
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