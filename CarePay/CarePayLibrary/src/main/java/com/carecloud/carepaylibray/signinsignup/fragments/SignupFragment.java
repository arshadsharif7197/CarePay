package com.carecloud.carepaylibray.signinsignup.fragments;

import android.content.Intent;
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
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.cognito.SignUpConfirmActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;

import com.carecloud.carepaylibray.demographics.models.DemographicDTO;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;


/**
 * Created by harish_revuri on 9/7/2016.
 * Signup screen.
 */
public class SignupFragment extends Fragment {

    private LinearLayout parentLayout;
    private ProgressBar  progressBar;

    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout passwordRepeatInputLayout;
    private EditText        emailText;
    private EditText        passwordText;
    private EditText        repeatPasswordText;

    private boolean isEmailEmpty;
    private boolean isPasswordEmpty;
    private boolean isRepeatPasswordEmpty;

    private Button   submitButton;
    private TextView accountExistTextView;

    private String userName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        setClickables(view);

        setEditTexts(view);

        isEmailEmpty = true;
        isPasswordEmpty = true;
        isRepeatPasswordEmpty = true;

        parentLayout.clearFocus();

        // TODO: 10/16/2016 remove
        emailText.setText("lsocolovici@yahoo.com");
        passwordText.setText("Liviu123_");
        repeatPasswordText.setText("Liviu123_");

        return view;
    }

    private void setClickables(View view) {
        submitButton = (Button) view.findViewById(R.id.submitSignupButton);
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
        accountExistTextView = (TextView) view.findViewById(R.id.signupAlreadyHaveAccountTextView);
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

        String hint;

        hint = getString(R.string.signin_signup_email_hint);
        emailInputLayout = (TextInputLayout) view.findViewById(R.id.emailTextInputLayout);
        emailInputLayout.setTag(hint);
        emailText = (EditText) view.findViewById(R.id.emailEditText);
        emailText.setTag(emailInputLayout);

        hint = getString(R.string.create_password_text);
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
    }

    private void setTextWatchers() {

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
                if (!isEmailEmpty) {
                    emailInputLayout.setError(null);
                    emailInputLayout.setErrorEnabled(false);
                }
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
                if (!isPasswordEmpty) {
                    passwordInputLayout.setError(null);
                    passwordInputLayout.setErrorEnabled(false);
                }
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
                if (!isRepeatPasswordEmpty) {
                    passwordRepeatInputLayout.setError(null);
                    passwordRepeatInputLayout.setErrorEnabled(false);
                }
                enableSignupButton();
            }
        });
    }

    /**
     * Set listener to capture focus change and toggle the hint text to caps on/off
     */
    public void setChangeFocusListeners() {
        emailText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        passwordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        repeatPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    private void setActionListeners() {
        emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
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
            emailInputLayout.setError(getString(R.string.signin_signup_error_empty_email));
        } else if (!isEmailValid) {
            emailInputLayout.setError(getString(R.string.signin_signup_error_invalid_email));
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
            error = getString(R.string.signin_signup_error_empty_password);
        } else if (!isPasswordValid) {
            error = getString(R.string.signup_error_passwords_invalid);
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
            passwordRepeatInputLayout.setError(getString(R.string.signin_signup_error_empty_repeat_password));
        } else if (isNotMachedPassw) {
            passwordRepeatInputLayout.setError(getString(R.string.signup_error_passwords_unmatched));
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
            CognitoAppHelper.signIn(userName, passwordText.getText().toString(),cognitoActionCallback);
        }
    }

    CognitoActionCallback cognitoActionCallback= new CognitoActionCallback() {
        @Override
        public void onLoginSuccess() {
            getDemographicInformation();
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onBeforeLogin() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoginFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(getContext(),
                    "Sign-in failed",
                    exceptionMessage);

        }
    };

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

    private void launchDemographics(DemographicDTO demographicDTO) {
        // do to Demographics
        Intent intent = new Intent(getActivity(), DemographicsActivity.class);
        // pass the object into the gson
        Gson gson = new Gson();
        intent.putExtra("demographics_model", gson.toJson(demographicDTO, DemographicDTO.class));

        startActivity(intent);
        getActivity().finish();
    }

    private void getDemographicInformation() {
        progressBar.setVisibility(View.VISIBLE);
        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicDTO> call = apptService.fetchDemographics();
        call.enqueue(new Callback<DemographicDTO>() {
            @Override
            public void onResponse(Call<DemographicDTO> call, Response<DemographicDTO> response) {
                DemographicDTO demographicDTO = response.body();
                progressBar.setVisibility(View.GONE);
                Log.v(LOG_TAG, "demographic info fetched");
                launchDemographics(demographicDTO);
            }

            @Override
            public void onFailure(Call<DemographicDTO> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(LOG_TAG, "failed fetching demogr info", t);
            }
        });
    }

    // cognito
    private void registerUser() {
        Log.v(LOG_TAG, "registerUser()");
        // Read user data and register
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();

        progressBar.setVisibility(View.VISIBLE);

        // TODO: 10/16/2016 remove
        CognitoAppHelper.signIn(emailText.getText().toString(), passwordText.getText().toString(), cognitoActionCallback);
        // TODO: 10/16/2016 uncomment
//        userName = emailText.getText().toString();
//        if (userName.length() > 0) {
//            userAttributes.addAttribute(CognitoAppHelper.getSignUpFieldsC2O().get("Email"), userName);
//            // add more attributes here is needed...
//        }
//        String password = passwordText.getText().toString();
//
//        CognitoAppHelper.getPool().signUpInBackground(userName,
//                password,
//                userAttributes,
//                null,
//                signUpHandler);
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

    SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            if (signUpConfirmationState) {
                // auto-confirmed; sign-in
                CognitoAppHelper.signIn(userName, passwordText.getText().toString(), cognitoActionCallback);
            } else {
                Log.v(LOG_TAG, "signUpConfirmationState == false");
                // User is not confirmed
                confirmSignUp(cognitoUserCodeDeliveryDetails); // not Confirmed; launch confirm activity
            }
        }

        @Override
        public void onFailure(Exception exception) {
            progressBar.setVisibility(View.INVISIBLE);
            String errorMsg = CognitoAppHelper.formatException(exception);

            SystemUtil.showDialogMessage(getActivity(),
                    "Sign up failed!",
                    errorMsg);
        }
    };
}