package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;

import static android.app.Activity.RESULT_OK;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


/**
 * Created by harish_revuri on 9/7/2016.
 * Signup screen
 */
public class SignupFragment extends Fragment {

    private TextInputLayout firstNameInputLayout, middleNameInputLayout, lastNameInputLayout, emailInputLayout, passwordInputLayout, passwordRepeatInputLayout;
    private EditText firstNameText;
    private EditText middleNameText;
    private EditText lastNameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText repeatPasswordText;
    private TextView accountExistTextView;
    private Button   submitButton;

    private boolean isValidFirstName;
    private boolean isValidLastName;
    private boolean isValidEmail;
    private boolean isValidPassword;
    private boolean isPasswordMatch;

    private String      userName;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // hide progress
        progressBar = (ProgressBar) view.findViewById(R.id.signupProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // set the toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(getContext(), SigninSignupActivity.class);
////                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                startActivity(intent);
////                getActivity().finish();
//                getActivity().onBackPressed();
//            }
//        });
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // set the buttons
        submitButton = (Button) view.findViewById(R.id.submitSignupButton);
        Typeface buttonFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/gotham_rounded_medium.otf");
        submitButton.setTypeface(buttonFontFamily);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllValid()) {
                    // request user registration
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

        isValidFirstName = true;
        isValidLastName = true;
        isValidPassword = true;
        isValidEmail = true;
        isPasswordMatch = true;

        setEditTexts(view);

        // for test
        emailText.setText("lvictor1979@gmail.com");
        passwordText.setText("Liviu123_");
        repeatPasswordText.setText("Liviu123_");

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

        setEditsTypefaces();

        setChangeFocusListeners();

        setActionListeners();
    }

    private void setActionListeners() {
        firstNameText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isValidFirstName = checkFirstName();
                    submitButton.setEnabled(areAllValid());
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
                    submitButton.setEnabled(areAllValid());
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
                    submitButton.setEnabled(areAllValid());
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
                    submitButton.setEnabled(areAllValid());
                    repeatPasswordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        repeatPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    isPasswordMatch = checkPasswordsMatch();
                    boolean isReadyForNext = areAllValid();
                    Log.v(LOG_TAG, " isREadyForNext = " + isReadyForNext
                            + " isValidFirstName=" + isValidFirstName
                            + " isValidLastNAme=" + isValidLastName
                            + " isEmailValid=" + isValidEmail
                            + " isPasswordValid=" + isValidPassword
                            + " isMAtchedPAssw=" + isPasswordMatch);

                    if (isReadyForNext) {
                        SystemUtil.hideSoftKeyboard(getActivity());
                        submitButton.setEnabled(true);
                        repeatPasswordText.clearFocus();
                        submitButton.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void setEditsTypefaces() {
        // TODO: 9/14/2016 change to use the Utilities
        Typeface editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        Typeface textViewFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");
        Typeface floatingFontfamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");

        firstNameInputLayout.setTypeface(editTextFontFamily);
        firstNameText.setTypeface(textViewFontFamily);

        middleNameInputLayout.setTypeface(editTextFontFamily);
        middleNameText.setTypeface(textViewFontFamily);

        lastNameInputLayout.setTypeface(editTextFontFamily);
        lastNameText.setTypeface(textViewFontFamily);

        emailInputLayout.setTypeface(editTextFontFamily);
        emailText.setTypeface(textViewFontFamily);

        passwordInputLayout.setTypeface(editTextFontFamily);
        passwordText.setTypeface(textViewFontFamily);

        passwordRepeatInputLayout.setTypeface(editTextFontFamily);
        repeatPasswordText.setTypeface(textViewFontFamily);

        accountExistTextView.setTypeface(textViewFontFamily);
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
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        middleNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                SystemUtil.handleHintChange(view, b);
            }
        });
        lastNameText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isValidLastName = checkLastName();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isValidEmail = checkEmail();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isValidPassword = checkPassword();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        repeatPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    isPasswordMatch = checkPasswordsMatch();
                    submitButton.setEnabled(areAllValid());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    private boolean checkFirstName() {
        boolean isFirstNameInvalid = StringUtil.isNullOrEmpty(this.firstNameText.getText().toString());
        String error = (isFirstNameInvalid ? "error" : null);
        firstNameInputLayout.setErrorEnabled(isFirstNameInvalid);
        firstNameInputLayout.setError(error);
        return !isFirstNameInvalid;
    }

    private boolean checkLastName() {
        boolean isLastNameInvalid = StringUtil.isNullOrEmpty(this.lastNameText.getText().toString());
        String error = (isLastNameInvalid ? "error" : null);
        lastNameInputLayout.setErrorEnabled(isLastNameInvalid);
        lastNameInputLayout.setError(error);
        return !isLastNameInvalid;
    }

    private boolean checkEmail() {
        String email = emailText.getText().toString();
        boolean isEmailInvalid = StringUtil.isNullOrEmpty(email) || !StringUtil.isValidmail(email);
        String error = (isEmailInvalid ? "error" : null);
        emailInputLayout.setErrorEnabled(isEmailInvalid);
        emailInputLayout.setError(error);
        return !isEmailInvalid;
    }

    private boolean checkPassword() {
        boolean isPassInvalid = StringUtil.isNullOrEmpty(passwordText.getText().toString());
        String error = (isPassInvalid ? "error" : null);
        passwordInputLayout.setErrorEnabled(isPassInvalid);
        passwordInputLayout.setError(error);
        return !isPassInvalid;
    }

    private boolean checkPasswordsMatch() {
        String password = passwordText.getText().toString();
        String repeatedPassword = repeatPasswordText.getText().toString();
        boolean isNotMachedPassw = StringUtil.isNullOrEmpty(password)
                || StringUtil.isNullOrEmpty(repeatedPassword)
                || !(password.equals(repeatedPassword));
        String error = (isNotMachedPassw ? "error" : null);
        passwordRepeatInputLayout.setErrorEnabled(isNotMachedPassw);
        passwordRepeatInputLayout.setError(error);
        return !isNotMachedPassw;
    }

    private boolean areAllValid() {
        return isValidFirstName
                && isValidLastName
                && isValidEmail
                && isValidPassword
                && isPasswordMatch;
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
                Log.v(LOG_TAG, "signUpConfirmationState == true");
                // confirmed; go to Demographics
                Intent intent = new Intent(getActivity(), DemographicsActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
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