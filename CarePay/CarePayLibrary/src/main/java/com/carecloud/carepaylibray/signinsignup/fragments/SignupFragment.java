package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.cognito.SignUpConfirmActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static android.app.Activity.RESULT_OK;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


/**
 * Created by harish_revuri on 9/7/2016.
 * Signup screen
 */
public class SignupFragment extends Fragment {

    private TextInputLayout firstNameInputLayout;
    private TextInputLayout middleNameInputLayout;
    private TextInputLayout lastNameInputLayout;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout passwordRepeatInputLayout;

    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText repeatPasswordText;
    private TextView accountExistTextView;

    private Button submitButton;

    private boolean isValidFirstName;
    private boolean isValidLastName;
    private boolean isValidEmail;
    private boolean isValidPassword;
    private boolean isPasswordMatch;

    private String       userName;
    private ProgressBar  progressBar;
    private LinearLayout parentLayout;

    private boolean isFirstNameEmpty;
    private boolean isLastNameEmpty;
    private boolean isEmailEmpty;
    private boolean isPasswordEmpty;
    private boolean isRepeatPasswordEmpty;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.v(LOG_TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        parentLayout = (LinearLayout) view.findViewById(R.id.signUpLl);

        // hide progress
        progressBar = (ProgressBar) view.findViewById(R.id.signupProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // set the toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // set the buttons
        submitButton = (Button) view.findViewById(R.id.submitSignupButton);
        Typeface buttonFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/gotham_rounded_medium.otf");
        submitButton.setTypeface(buttonFontFamily);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check all
                isValidFirstName = checkFirstName();
                isValidLastName = checkLastName();
                isValidEmail = checkEmail();
                isValidPassword = checkPassword();
                isPasswordMatch = checkPasswordsMatch();

                if (areAllValid()) {
                    // request user registration ony if all fiels are valid
                    registerUser();
                }
            }
        });
        submitButton.setEnabled(false);

        accountExistTextView = (TextView) view.findViewById(R.id.oldUserTextView);
        accountExistTextView = (TextView) view.findViewById(R.id.oldUserTextView);
        accountExistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                SigninFragment fragment = (SigninFragment) fm.findFragmentByTag(SigninFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new SigninFragment();
                }
                fm.beginTransaction()
                        .replace(R.id.layoutSigninSignup, fragment, SigninFragment.class.getSimpleName())
                        .commit();
            }
        });

        isValidFirstName = false;
        isValidLastName = false;
        isValidPassword = false;
        isValidEmail = false;
        isPasswordMatch = false;

        isFirstNameEmpty = true;
        isLastNameEmpty = true;
        isEmailEmpty = true;
        isPasswordEmpty = true;
        isRepeatPasswordEmpty = true;

        setEditTexts(view);

        parentLayout.clearFocus();

        return view;
    }

    private void setEditTexts(View view) {

        String hint = getString(R.string.firstname_text);
        firstNameInputLayout = (TextInputLayout) view.findViewById(R.id.firstNameTextInputLayout);
        firstNameInputLayout.setTag(hint);
        firstNameText = (EditText) view.findViewById(R.id.firstNameEditText);
        firstNameText.setTag(firstNameInputLayout);

        hint = getString(R.string.middlename_text);
        middleNameInputLayout = (TextInputLayout) view.findViewById(R.id.middleNameTextInputLayout);
        middleNameInputLayout.setTag(hint);
        middleNameText = (EditText) view.findViewById(R.id.middleNameEditText);
        middleNameText.setTag(middleNameInputLayout);

        hint = getString(R.string.lastname_text);
        lastNameInputLayout = (TextInputLayout) view.findViewById(R.id.lastNameTextInputLayout);
        lastNameInputLayout.setTag(hint);
        lastNameText = (EditText) view.findViewById(R.id.lastNameEditText);
        lastNameText.setTag(lastNameInputLayout);

        hint = getString(R.string.email_text);
        emailInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        emailInputLayout.setTag(hint);
        emailText = (EditText) view.findViewById(R.id.emailEditText);
        emailText.setTag(emailInputLayout);

        hint = getString(R.string.password_text);
        passwordInputLayout = (TextInputLayout) view.findViewById(R.id.createPasswordTextInputLayout);
        passwordInputLayout.setTag(hint);
        passwordText = (EditText) view.findViewById(R.id.createPasswordEditText);
        passwordText.setTag(passwordInputLayout);

        hint = getString(R.string.repeat_password_text);
        passwordRepeatInputLayout = (TextInputLayout) view.findViewById(R.id.repeatPasswordTextInputLayout);
        passwordRepeatInputLayout.setTag(hint);
        repeatPasswordText = (EditText) view.findViewById(R.id.repeatPasswordEditText);
        repeatPasswordText.setTag(passwordRepeatInputLayout);

        setTypefaces();

        setChangeFocusListeners();

        setActionListeners();

        setTextWatchers();

        parentLayout.clearFocus();
    }

    private void setTextWatchers() {
        firstNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameText.getText().toString());
                enableSignupButton();
            }
        });

        lastNameText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameText.getText().toString());
                enableSignupButton();
            }
        });

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEmailEmpty = StringUtil.isNullOrEmpty(emailText.getText().toString());
                enableSignupButton();
            }
        });

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isPasswordEmpty = StringUtil.isNullOrEmpty(passwordText.getText().toString());
                enableSignupButton();
            }
        });

        repeatPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isRepeatPasswordEmpty = StringUtil.isNullOrEmpty(repeatPasswordText.getText().toString());
                enableSignupButton();
            }
        });
    }

    /**
     * Set listener to capture focus change and toggle the hint text to caps on/off
     */
    public void setChangeFocusListeners() {
        firstNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) { // when loosing focus, validate as well
                    isValidFirstName = checkFirstName();
                } else { // show the keyboard
                    firstNameText.requestFocus();
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        middleNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                SystemUtil.handleHintChange(view, b);
                if(b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
            }
        });
        lastNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isValidLastName = checkLastName();
                } else {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isValidEmail = checkEmail();
                } else {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isValidPassword = checkPassword();
                    if (!isRepeatPasswordEmpty) {  // check reactively if the match password, if repeated not empty
                        isPasswordMatch = checkPasswordsMatch();
                    }
                } else {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        repeatPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isPasswordMatch = checkPasswordsMatch();
                } else {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    private void setActionListeners() {
        firstNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidFirstName = checkFirstName();
                    middleNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        middleNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) { // no validations for middle name
                    lastNameText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        lastNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidLastName = checkLastName();
                    emailText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidEmail = checkEmail();
                    passwordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidPassword = checkPassword();
                    if (!isRepeatPasswordEmpty) { // check reactively if the match password, if repeated not empty
                        isPasswordMatch = checkPasswordsMatch();
                    }

                    repeatPasswordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        repeatPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    isPasswordMatch = checkPasswordsMatch();
                    repeatPasswordText.clearFocus();
                    parentLayout.requestFocus();
                    SystemUtil.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
    }

    private void setTypefaces() {
        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), firstNameInputLayout);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), firstNameText);

        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), middleNameInputLayout);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), middleNameText);

        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), lastNameInputLayout);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), lastNameText);

        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), emailInputLayout);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), emailText);

        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), passwordInputLayout);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), passwordText);

        SystemUtil.setProximaNovaRegularTypefaceLayout(getActivity(), passwordRepeatInputLayout);
        SystemUtil.setProximaNovaRegularTypeface(getActivity(), repeatPasswordText);

        SystemUtil.setProximaNovaRegularTypeface(getActivity(), accountExistTextView);
    }

    private boolean checkFirstName() { // valid  means non-empty
        isFirstNameEmpty = StringUtil.isNullOrEmpty(firstNameText.getText().toString());
        String error = (isFirstNameEmpty ? getString(R.string.signup_error_empty_first_name) : null);
        firstNameInputLayout.setErrorEnabled(isFirstNameEmpty);
        firstNameInputLayout.setError(error);
        return !isFirstNameEmpty;
    }

    private boolean checkLastName() {
        isLastNameEmpty = StringUtil.isNullOrEmpty(lastNameText.getText().toString());
        String error = (isLastNameEmpty ? getString(R.string.signup_error_empty_last_name) : null);
        lastNameInputLayout.setErrorEnabled(isLastNameEmpty);
        lastNameInputLayout.setError(error);
        return !isLastNameEmpty;
    }

    private boolean checkEmail() {
        String email = emailText.getText().toString();
        isEmailEmpty = StringUtil.isNullOrEmpty(email);
        boolean isEmailValid = StringUtil.isValidmail(email);
        emailInputLayout.setErrorEnabled(isEmailEmpty || !isEmailValid); // enable for error if either empty or invalid email
        if (isEmailEmpty) {
            emailInputLayout.setError(getString(R.string.signin_signup_error_empty_email));
        } else if (!isEmailValid) {
            emailInputLayout.setError(getString(R.string.signin_signup_error_invalid_email));
        } else {
            emailInputLayout.setError(null);
        }
        return !isEmailEmpty && isEmailValid;
    }

    private boolean checkPassword() {
        isPasswordEmpty = StringUtil.isNullOrEmpty(passwordText.getText().toString());
        String error = (isPasswordEmpty ? getString(R.string.signin_signup_error_empty_password) : null);
        passwordInputLayout.setErrorEnabled(isPasswordEmpty);
        passwordInputLayout.setError(error);
        return !isPasswordEmpty;
    }

    private boolean checkPasswordsMatch() {
        String password = passwordText.getText().toString();
        String repeatedPassword = repeatPasswordText.getText().toString();

        isPasswordEmpty = StringUtil.isNullOrEmpty(password);
        isRepeatPasswordEmpty = StringUtil.isNullOrEmpty(repeatedPassword);
        boolean isNotMachedPassw = !(password.equals(repeatedPassword));

        passwordRepeatInputLayout.setErrorEnabled(isPasswordEmpty || isRepeatPasswordEmpty || isNotMachedPassw);

        if (isRepeatPasswordEmpty) {
            passwordRepeatInputLayout.setError(getString(R.string.signin_signup_error_empty_repeat_password));
        } else if (isNotMachedPassw) {
            passwordRepeatInputLayout.setError(getString(R.string.signup_error_passwords_unmatched));
        } else {
            passwordRepeatInputLayout.setError(null);
        }

        return !(isPasswordEmpty || isRepeatPasswordEmpty || isNotMachedPassw);
    }

    private boolean areAllValid() {
        return isValidFirstName
                && isValidLastName
                && isValidEmail
                && isValidPassword
                && isPasswordMatch;
    }

    private void enableSignupButton() {
        boolean areAllNonEmpty =
                !(isFirstNameEmpty ||
                        isLastNameEmpty ||
                        isEmailEmpty ||
                        isPasswordEmpty ||
                        isRepeatPasswordEmpty);
        submitButton.setEnabled(areAllNonEmpty);
    }

    private void reset() {
        firstNameText.setText("");
        firstNameInputLayout.setErrorEnabled(false);
        firstNameInputLayout.setError(null);

        middleNameText.setText("");

        lastNameText.setText("");
        lastNameInputLayout.setEnabled(false);
        lastNameInputLayout.setError(null);

        lastNameText.setText("");
        lastNameInputLayout.setErrorEnabled(false);
        lastNameInputLayout.setError(null);

        emailText.setText("");
        emailInputLayout.setErrorEnabled(false);
        emailInputLayout.setError(null);

        passwordText.setText("");
        passwordInputLayout.setErrorEnabled(false);
        passwordInputLayout.setError(null);

        repeatPasswordText.setText("");
        passwordRepeatInputLayout.setErrorEnabled(false);
        passwordRepeatInputLayout.setError(null);

        isFirstNameEmpty = true;
        isLastNameEmpty = true;
        isEmailEmpty = true;
        isPasswordEmpty = true;
        isRepeatPasswordEmpty = true;

        isValidFirstName = false;
        isValidLastName = false;
        isValidEmail = false;
        isValidPassword = false;
        isPasswordMatch = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                // confirmed; (auto)sign-in
                CognitoAppHelper.getPool().getUser(userName).getSessionInBackground(authenticationHandler);
            }
        }
    }

    @Override
    public void onStop() {
        reset();
        super.onStop();
    }

    // cognito
    private void registerUser() {
        Log.v(LOG_TAG, "registerUser()");
        // Read user data and register
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        progressBar.setVisibility(View.VISIBLE);

        userName = emailText.getText().toString();
        if (userName.length() > 0) {
            userAttributes.addAttribute(CognitoAppHelper.getSignUpFieldsC2O().get("Email"), userName);
            // add more attributes here is needed...
        }
        String password = passwordText.getText().toString();

        CognitoAppHelper.getPool().signUpInBackground(userName,
                                                      password,
                                                      userAttributes,
                                                      null,
                                                      signUpHandler);
    }

    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(getActivity(), SignUpConfirmActivity.class);
        intent.putExtra("source", "signup");
        intent.putExtra("name", userName);
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, 10);
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        Log.v(LOG_TAG, "getUserAuthentication()");

        userName = username;
        if (username != null) {
            CognitoAppHelper.setUser(username);
        }

        String password = passwordText.getText().toString();
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            if (signUpConfirmationState) {
                // auto-confirmed; sign-in
                CognitoAppHelper.getPool().getUser(userName).getSessionInBackground(authenticationHandler);
            } else {
                Log.v(LOG_TAG, "signUpConfirmationState == false");
                // User is not confirmed
                confirmSignUp(cognitoUserCodeDeliveryDetails); // not Confirmed; launch confirm activity
            }
        }

        @Override
        public void onFailure(Exception exception) {
            progressBar.setVisibility(View.INVISIBLE);
            SystemUtil.showDialogMessage(getActivity(),
                                         "Sign up failed!",
                                         "Invalid id or password");
            Log.e(LOG_TAG, CognitoAppHelper.formatException(exception));
        }
    };

    private AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {
            Log.v(LOG_TAG, "auth success");
            CognitoAppHelper.setCurrSession(userSession);
            CognitoAppHelper.newDevice(newDevice);

            progressBar.setVisibility(View.INVISIBLE);

            // do to Demographics
            Intent intent = new Intent(getActivity(), DemographicsActivity.class);
            startActivity(intent);
            getActivity().finish();

        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String userId) {
            Log.v(LOG_TAG, "getAuthenticationDetails()");
            getUserAuthentication(authenticationContinuation, userId);
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
            progressBar.setVisibility(View.INVISIBLE);
            Log.e(LOG_TAG, exception.getLocalizedMessage());
        }
    };
}