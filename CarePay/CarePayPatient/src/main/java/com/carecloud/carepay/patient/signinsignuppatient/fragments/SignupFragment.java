package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.cognito.SignUpConfirmActivity;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInLablesDTO;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInSignUpDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static android.app.Activity.RESULT_OK;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by harish_revuri on 9/7/2016.
 * Signup screen.
 */
public class SignupFragment extends BaseFragment {

    private static final String TAG = "SignupFragment";
    private SignInSignUpDTO signInSignUpDTO;
    private SignInLablesDTO signInLablesDTO;
    private LinearLayout    parentLayout;
    private ProgressBar     progressBar;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout passwordRepeatInputLayout;
    private EditText        emailText;
    private EditText        passwordText;
    private EditText        repeatPasswordText;
    private boolean         isEmailEmpty;
    private boolean         isPasswordEmpty;
    private boolean         isRepeatPasswordEmpty;
    private Button          submitButton;
    private TextView        accountExistTextView;
    private TextView passwordFormatHint;
    private String          userName;
    private SignUpHandler           signUpHandler          = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            if (signUpConfirmationState) {
                // auto-confirmed; sign-in
                getAppAuthorizationHelper().signIn(userName, passwordText.getText().toString(), cognitoActionCallback);
            } else {
                Log.v(LOG_TAG, "signUpConfirmationState == false");
                // User is not confirmed
                confirmSignUp(cognitoUserCodeDeliveryDetails); // not Confirmed; launch confirm activity
            }
        }

        @Override
        public void onFailure(Exception exception) {
            hideProgressDialog();
            String errorMsg = getAppAuthorizationHelper().formatException(exception);
            showErrorNotification(errorMsg);
            Log.e(TAG, exception.getMessage());
//            SystemUtil.showFailureDialogMessage(getActivity(),
//                                         "Sign up failed!",//TODO this should not be hardcoded string
//                                         errorMsg);
        }
    };
    private WorkflowServiceCallback signUpWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
    private CognitoActionCallback   cognitoActionCallback  = new CognitoActionCallback() {
        @Override
        public void onLoginSuccess() {
            progressBar.setVisibility(View.INVISIBLE);
            TransitionDTO transitionDTO = signInSignUpDTO.getMetadata().getTransitions().getAuthenticate();
            getWorkflowServiceHelper().execute(transitionDTO, signUpWorkflowCallback);
        }

        @Override
        public void onBeforeLogin() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoginFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(null);
            Log.e(TAG, exceptionMessage);
//            SystemUtil.showFailureDialogMessage(getContext(),
//                                         "Sign-in failed",
//                                         exceptionMessage);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        signInSignUpDTO = ((SigninSignupActivity) getActivity()).getSignInSignUpDTO();
        signInLablesDTO = signInSignUpDTO.getMetadata().getLabels();
        parentLayout = (LinearLayout) view.findViewById(R.id.signUpLl);

        // hide progress
        progressBar = (ProgressBar) view.findViewById(R.id.signupProgressBar);
        progressBar.setVisibility(View.INVISIBLE);

        // set the toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.signup_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.signup_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // set the buttons
        setClickables(view);

        setEditTexts(view);

        isEmailEmpty = true;
        isPasswordEmpty = true;
        isRepeatPasswordEmpty = true;

        parentLayout.clearFocus();

        return view;
    }

    private void setClickables(View view) {
        submitButton = (Button) view.findViewById(R.id.submitSignupButton);
        submitButton.setText(signInLablesDTO.getSignupButton());
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllValid()) {
                    // request user registration ony if all fiels are valid
                    registerUser();
                }
            }
        });
        submitButton.setEnabled(false);

        accountExistTextView = (TextView) view.findViewById(R.id.signupAlreadyHaveAccountTextView);
        accountExistTextView.setText(signInLablesDTO.getAlreadyHaveAccountLink());
        accountExistTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset();
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
    }

    private void setEditTexts(View view) {

        emailInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        emailInputLayout.setTag(signInLablesDTO.getEmail());
        emailText = (EditText) view.findViewById(R.id.emailEditText);
        emailText.setHint(signInLablesDTO.getEmail());
        emailText.setTag(emailInputLayout);

        passwordInputLayout = (TextInputLayout) view.findViewById(R.id.createPasswordTextInputLayout);
        passwordInputLayout.setTag(signInLablesDTO.getCreatePassword());
        passwordText = (EditText) view.findViewById(R.id.createPasswordEditText);
        passwordText.setHint(signInLablesDTO.getCreatePassword());
        passwordText.setTag(passwordInputLayout);

        passwordRepeatInputLayout = (TextInputLayout) view.findViewById(R.id.repeatPasswordTextInputLayout);
        passwordRepeatInputLayout.setTag(signInLablesDTO.getRepeatPassword());
        repeatPasswordText = (EditText) view.findViewById(R.id.repeatPasswordEditText);
        repeatPasswordText.setHint(signInLablesDTO.getRepeatPassword());
        repeatPasswordText.setTag(passwordRepeatInputLayout);


        passwordFormatHint= (TextView) view.findViewById(R.id.singupPasswordFormatHint);
        passwordFormatHint.setText(signInLablesDTO.getPasswordHintText());
        setTypefaces();

        setChangeFocusListeners();

        setActionListeners();

        setTextWatchers();
    }

    private void setTextWatchers() {

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isEmailEmpty = StringUtil.isNullOrEmpty(emailText.getText().toString());
                if (!isEmailEmpty) {
                    emailInputLayout.setError(null);
                    emailInputLayout.setErrorEnabled(false);
                }
                enableSignupButton();
            }
        });

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isPasswordEmpty = StringUtil.isNullOrEmpty(passwordText.getText().toString());
                if (!isPasswordEmpty) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setErrorEnabled(false);
                }
                enableSignupButton();
            }
        });

        repeatPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                isRepeatPasswordEmpty = StringUtil.isNullOrEmpty(repeatPasswordText.getText().toString());
                if (!isRepeatPasswordEmpty) {
                    passwordRepeatInputLayout.setError(null);
                    passwordRepeatInputLayout.setErrorEnabled(false);
                }
                enableSignupButton();
            }
        });
    }

    private void setChangeFocusListeners() {
        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
        repeatPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, hasFocus);
            }
        });
    }

    private void setActionListeners() {
        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    passwordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    repeatPasswordText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        repeatPasswordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
        SystemUtil.setProximaNovaSemiboldTextInputLayout(getActivity(), emailInputLayout);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), emailText);

        SystemUtil.setProximaNovaSemiboldTextInputLayout(getActivity(), passwordInputLayout);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), passwordText);

        SystemUtil.setProximaNovaSemiboldTextInputLayout(getActivity(), passwordRepeatInputLayout);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), repeatPasswordText);

        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), accountExistTextView);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), submitButton);
    }

    private boolean checkEmail() {
        String email = emailText.getText().toString();
        isEmailEmpty = StringUtil.isNullOrEmpty(email);
        boolean isEmailValid = StringUtil.isValidmail(email);
        emailInputLayout.setErrorEnabled(isEmailEmpty || !isEmailValid); // enable for error if either empty or invalid email
        if (isEmailEmpty) {
            emailInputLayout.setError(signInLablesDTO.getPleaseEnterEmail());
        } else if (!isEmailValid) {
            emailInputLayout.setError(signInLablesDTO.getInvalidEmail());
        } else {
            emailInputLayout.setError(null);
        }
        return !isEmailEmpty && isEmailValid;
    }

    private boolean checkPassword() {
        String passwordString = passwordText.getText().toString();
        isPasswordEmpty = StringUtil.isNullOrEmpty(passwordString);
        boolean isPasswordValid = StringUtil.isValidPassword(passwordString);

        passwordInputLayout.setErrorEnabled(isPasswordEmpty || !isPasswordValid);
        String error;
        if (isPasswordEmpty) {
            error = signInLablesDTO.getPleaseEnterPassword();
        } else if (!isPasswordValid) {
            error = signInLablesDTO.getInvalidPassword();
        } else {
            error = null;
        }
        passwordInputLayout.setError(error);

        return !isPasswordEmpty && isPasswordValid;
    }

    private boolean checkPasswordsMatch() {
        String password = passwordText.getText().toString();
        String repeatedPassword = repeatPasswordText.getText().toString();

        isPasswordEmpty = StringUtil.isNullOrEmpty(password);
        isRepeatPasswordEmpty = StringUtil.isNullOrEmpty(repeatedPassword);
        boolean isNotMachedPassw = !(password.equals(repeatedPassword));

        passwordRepeatInputLayout.setErrorEnabled(isPasswordEmpty || isRepeatPasswordEmpty || isNotMachedPassw);

        if (isRepeatPasswordEmpty) {
            passwordRepeatInputLayout.setError(signInLablesDTO.getRepeatPasswordIsEmpty());
        } else if (isNotMachedPassw) {
            passwordRepeatInputLayout.setError(signInLablesDTO.getPasswordsDoNotMatch());
        } else {
            passwordRepeatInputLayout.setError(null);
        }

        return !(isPasswordEmpty || isRepeatPasswordEmpty || isNotMachedPassw);
    }

    private boolean areAllValid() {
        // check in reverse order that they are placed on the screen
        boolean isPasswordMatch = checkPasswordsMatch();
        if (!isPasswordMatch) {
            repeatPasswordText.requestFocus();
        }
        boolean isValidPassword = checkPassword();
        if (!isValidPassword) {
            passwordText.requestFocus();
        }
        boolean isValidEmail = checkEmail();
        if (!isValidEmail) {
            emailText.requestFocus();
        }

        return isValidEmail && isValidPassword && isPasswordMatch;
    }

    private void enableSignupButton() {
        boolean areAllNonEmpty = !(isEmailEmpty ||
                isPasswordEmpty ||
                isRepeatPasswordEmpty);
        submitButton.setEnabled(areAllNonEmpty);
    }

    private void reset() {
        emailText.clearFocus();
        emailText.setText("");
        emailInputLayout.setErrorEnabled(false);
        emailInputLayout.setError(null);

        passwordText.clearFocus();
        passwordText.setText("");
        passwordInputLayout.setErrorEnabled(false);
        passwordInputLayout.setError(null);

        repeatPasswordText.clearFocus();
        repeatPasswordText.setText("");
        passwordRepeatInputLayout.setErrorEnabled(false);
        passwordRepeatInputLayout.setError(null);

        isEmailEmpty = true;
        isPasswordEmpty = true;
        isRepeatPasswordEmpty = true;
    }

    /**
     * Receives the result from the confirm email activity; (just in case we need confirmation)
     *
     * @param requestCode The request code.
     * @param resultCode  The result code.
     * @param data        The passed data.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            // confirmed; (auto)sign-in
            getAppAuthorizationHelper().signIn(userName, passwordText.getText().toString(), cognitoActionCallback);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            SystemUtil.hideSoftKeyboard(getActivity()); // hide the keyboard (for courtesy)
            reset();
            getActivity().onOptionsItemSelected(item);
            return true;
        }
        return false;
    }

    // cognito
    private void registerUser() {
        // Read user data and register
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        progressBar.setVisibility(View.VISIBLE);

        userName = emailText.getText().toString();
        if (userName.length() > 0) {
            userAttributes.addAttribute(getAppAuthorizationHelper().getSignUpFieldsC2O().get("Email"), userName);
            // add more attributes here is needed...
        }
        String password = passwordText.getText().toString();

        getAppAuthorizationHelper().getPool().signUpInBackground(userName,
                                                      password,
                                                      userAttributes,
                                                      null,
                                                      signUpHandler);
    }

    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(getActivity(), SignUpConfirmActivity.class);
        intent.putExtra("source", signInLablesDTO.getSignUp());
        intent.putExtra("signInLablesDTO",signInLablesDTO);
        intent.putExtra("name", userName);
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, 10);
    }
}