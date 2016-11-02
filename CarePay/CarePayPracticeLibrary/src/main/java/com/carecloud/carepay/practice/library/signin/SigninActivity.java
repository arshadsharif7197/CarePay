package com.carecloud.carepay.practice.library.signin;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.practice.library.signin.dtos.LanguageOptionDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninLabelsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;

import com.carecloud.carepaylibray.utils.StringUtil;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import com.carecloud.carepaylibray.utils.SystemUtil;
import java.util.ArrayList;
import java.util.List;





/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the practice app
 * Cogtino SDK use for user authentication
 * On success authentication screen will navigate next screen from the transition Json
 * On failed showing the authentication failure dialog with no navigation
 */
public class SigninActivity extends BasePracticeActivity {

    private TextView signinButton;
    private TextView forgotPasswordButton;
    private TextView signinTitle;

    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;

    private boolean   isEmptyEmail;
    private boolean   isEmptyPassword;
    private ImageView rightarrow;

    private String emailLabel;
    private String passwordLabel;

    private List<String> languages = new ArrayList<String>();

    SigninDTO signinDTO;
    private Spinner langSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        CognitoAppHelper.init(this);
        ApplicationPreferences.createPreferences(this); // init preferences
        signinDTO = getConvertedDTO(SigninDTO.class);
        setContentView(R.layout.activity_signin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSystemUiVisibility();
        progressBar = (ProgressBar) findViewById(R.id.signInProgress);
        progressBar.setVisibility(View.INVISIBLE);

        initViews();
        setEditTexts();
        setClicables();
        setTypeFace();

        isEmptyEmail = true;
        isEmptyPassword = true;
    }

    /**
     * Initailizing the view
     */
    public void initViews() {
        signinButton = (TextView) findViewById(R.id.signinTextview);
        rightarrow = (ImageView) findViewById(R.id.rightarrow);
        forgotPasswordButton = (TextView) findViewById(R.id.forgot_passwordTextview);
        passwordEditText = (EditText) findViewById(R.id.passwordpracticeEditText);
        emailEditText = (EditText) findViewById(R.id.signinEmailpracticeEditText);
        signInEmailTextInputLayout = (TextInputLayout) findViewById(R.id.signInEmailTextInputLayout);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        signinTitle = (TextView) findViewById(R.id.signinTitleTextview);

        // set the language spinner
        int langaugelistsize = signinDTO.getPayload().getPracticeModeSignin().getLanguage().getOptions().size();
        LanguageOptionDTO defaultLangOption = null;
        int indexDefault = 0;
        for (int i = 0; i < langaugelistsize; i++) {
            LanguageOptionDTO languageOption = signinDTO.getPayload().getPracticeModeSignin().getLanguage().getOptions().get(i);
            languages.add(i, languageOption.getCode().toUpperCase());
            if (languageOption.getDefault()) {
                defaultLangOption = languageOption;
                indexDefault = i;
            }
        }
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.home_spinner_item, languages);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        langSpinner = (Spinner) findViewById(R.id.signinLangSpinner);
        langSpinner.setAdapter(spinnerArrayAdapter);
        if (defaultLangOption != null) { // this should be always true, as there's always a default option
            langSpinner.setSelection(indexDefault);
            ApplicationPreferences.Instance.setUserLanguage(defaultLangOption.getCode());
        }

        initializeLebals();
        // disable sign-in button
        setEnabledSigninButton(false);
    }

    private void initializeLebals() {
        if (signinDTO != null) {
            SigninLabelsDTO signinLabelsDTO = signinDTO.getMetadata().getLabels();
            if (signinLabelsDTO != null) {
                signinButton.setText(signinLabelsDTO.getSigninButton());
                signinTitle.setText(signinLabelsDTO.getWelcomeSigninText());
                forgotPasswordButton.setText(signinLabelsDTO.getForgotPassword());
                emailLabel = StringUtil.getLabelForView(signinLabelsDTO.getSigninEmailAddress());
                passwordLabel = StringUtil.getLabelForView(signinLabelsDTO.getSigninPassword());
                passwordEditText.setHint(passwordLabel);
                emailEditText.setHint(emailLabel);
            }
        }
    }

    private void setEnabledSigninButton(boolean enabled) {
        if (!enabled) {
            signinButton.setTextColor(signinButton.getTextColors().withAlpha(50));
            rightarrow.setAlpha(50);
        } else {
            signinButton.setTextColor(signinButton.getTextColors().withAlpha(255));
            rightarrow.setAlpha(255);
        }
        signinButton.setEnabled(enabled);
    }

    private void setClicables() {

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areAllValid()) {
                    signInUser();
                }
            }
        });

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // save selected in preferences
                ApplicationPreferences.Instance.setUserLanguage(languages.get(position).toLowerCase());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private boolean checkEmail() {
        String email = emailEditText.getText().toString();
        isEmptyEmail = StringUtil.isNullOrEmpty(email);
        boolean isEmailValid = StringUtil.isValidmail(email);
        signInEmailTextInputLayout.setErrorEnabled(isEmptyEmail || !isEmailValid); // enable for error if either empty or invalid email
        if (isEmptyEmail) {
            signInEmailTextInputLayout.setError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_empty_email));
        } else if (!isEmailValid) {
            signInEmailTextInputLayout.setError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_invalid_email));
        } else {
            signInEmailTextInputLayout.setError(null);
        }
        return !isEmptyEmail && isEmailValid;
    }

    private boolean checkPassword() {
        isEmptyPassword = StringUtil.isNullOrEmpty(passwordEditText.getText().toString());
        String error = (isEmptyPassword ? getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_empty_password) : null);
        passwordTextInputLayout.setErrorEnabled(isEmptyPassword);
        passwordTextInputLayout.setError(error);
        return !isEmptyPassword;
    }

    private boolean areAllValid() {
        boolean isPasswordValid = checkPassword();
        if (!isPasswordValid) {
            passwordEditText.requestFocus();
        }
        boolean isEmailValid = checkEmail();
        if (!isEmailValid) {
            emailEditText.requestFocus();
        }
        return isEmailValid && isPasswordValid;
    }

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
                isEmptyEmail = StringUtil.isNullOrEmpty(emailEditText.getText().toString());
                if (!isEmptyEmail) { // clear the error
                    signInEmailTextInputLayout.setError(null);
                    signInEmailTextInputLayout.setErrorEnabled(false);
                }
                enableSigninButton();
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
                isEmptyPassword = StringUtil.isNullOrEmpty(passwordEditText.getText().toString());
                if (isEmptyPassword) {
                    passwordTextInputLayout.setError(null);
                    passwordTextInputLayout.setErrorEnabled(false);
                }
                enableSigninButton();
            }
        });
    }


    private void setEditTexts() {
        signInEmailTextInputLayout.setTag(emailLabel);
        emailEditText.setTag(signInEmailTextInputLayout);

        passwordTextInputLayout.setTag(passwordLabel);
        passwordEditText.setTag(passwordTextInputLayout);

        setTextListeners();
        setChangeFocusListeners();


        emailEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    private void setChangeFocusListeners() {
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(SigninActivity.this);
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(SigninActivity.this);
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
    }

    private void enableSigninButton() {
        boolean areAllNonEmpty = !(isEmptyEmail || isEmptyPassword);
        setEnabledSigninButton(areAllNonEmpty);
    }

    private void signInUser() {
        Log.v(LOG_TAG, "sign in user");
        String userName = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        CognitoAppHelper.signIn(userName, password, cognitoActionCallback);

    }

    CognitoActionCallback cognitoActionCallback = new CognitoActionCallback() {
        @Override
        public void onLoginSuccess() {
            progressBar.setVisibility(View.INVISIBLE);
            //launchHomescreen();
            WorkflowServiceHelper.getInstance().execute(signinDTO.getMetadata().getTransitions().getAuthenticate(), signinCallback);
        }
        //launchHomescreen


        @Override
        public void onBeforeLogin() {
            SystemUtil.hideSoftKeyboard(SigninActivity.this);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoginFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(SigninActivity.this,
                                         "Sign-in failed",
                                         "Invalid user id or password");

        }
    };

    WorkflowServiceCallback signinCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

        }
    };

    private void launchHomescreen() {
        Intent intent = new Intent(this, CloverMainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void setSystemUiVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }


    public void setTypeFace() {
        setProximaNovaRegularTypeface(this, emailEditText);
        setProximaNovaRegularTypeface(this, passwordEditText);
    }
}
