package com.carecloud.carepay.practice.library.signin;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.signin.dtos.LanguageOptionDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninLabelsDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeLabelsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.utils.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import java.util.Map;

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
    private TextView gobackButton;

    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;

    private boolean isEmptyEmail;
    private boolean isEmptyPassword;
    private ImageView homeButton;

    private Button signInButton;

    private String emailLabel;
    private String passwordLabel;

    private List<String> languages = new ArrayList<>();

    private SigninDTO signinDTO;
    private SigninPatientModeDTO signinPatientModeDTO;
    private Spinner langSpinner;
    private List<String> modeSwitchOptions = new ArrayList<>();

    public enum SignInScreenMode {
        PRACTICE_MODE_SIGNIN, PATIENT_MODE_SIGNIN
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        SignInScreenMode signinScreenMode = SignInScreenMode.PRACTICE_MODE_SIGNIN;
        if (ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            signinDTO = getConvertedDTO(SigninDTO.class);
            if (signinDTO != null && signinDTO.getPayload() != null && signinDTO.getPayload().getPracticeModeSignin() != null && signinDTO.getPayload().getPracticeModeSignin().getCognito() != null) {
                ApplicationMode.getInstance().setCognitoDTO(signinDTO.getPayload().getPracticeModeSignin().getCognito());
                CognitoAppHelper.init(getApplicationContext());
                signinScreenMode = SignInScreenMode.valueOf(signinDTO.getState().toUpperCase());
            }
        } else if (ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            signinPatientModeDTO = getConvertedDTO(SigninPatientModeDTO.class);
            if (signinPatientModeDTO != null && signinPatientModeDTO.getPayload() != null
                    && signinPatientModeDTO.getPayload().getPatientModeSigninData().getCognito() != null) {
                ApplicationMode.getInstance().setCognitoDTO(signinPatientModeDTO.getPayload().getPatientModeSigninData().getCognito());
                CognitoAppHelper.init(getApplicationContext());
                signinScreenMode = SignInScreenMode.valueOf(signinPatientModeDTO.getState().toUpperCase());
            }
        }
        ApplicationPreferences.createPreferences(this); // init preferences
        setContentView(R.layout.activity_signin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSystemUiVisibility();
        initViews(signinScreenMode);
        setEditTexts();
        setClicables();
        setTypeFace();

        changeScreenMode(signinScreenMode);
        isEmptyEmail = true;
        isEmptyPassword = true;

        // TODO: 11/17/2016
        emailEditText.setText("practice@cc.com");
        passwordEditText.setText("Practice123!");
    }

    /**
     * Initailizing the view
     */
    public void initViews(SignInScreenMode signInScreenMode) {
//        signinButton = (TextView) findViewById(R.id.signinTextview);
        signInButton = (Button) findViewById(R.id.signinButton);
        homeButton = (ImageView) findViewById(R.id.signInHome);
        gobackButton = (TextView) findViewById(R.id.goBackButtonTextview);
        forgotPasswordButton = (TextView) findViewById(R.id.forgot_passwordTextview);
        passwordEditText = (EditText) findViewById(R.id.passwordpracticeEditText);
        emailEditText = (EditText) findViewById(R.id.signinEmailpracticeEditText);
        langSpinner = (Spinner) findViewById(R.id.signinLangSpinner);
        signInEmailTextInputLayout = (TextInputLayout) findViewById(R.id.signInEmailTextInputLayout);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        signinTitle = (TextView) findViewById(R.id.signinTitleTextview);

        if (signInScreenMode == SignInScreenMode.PRACTICE_MODE_SIGNIN) {
            int languageListSize = signinDTO.getPayload().getPracticeModeSignin().getLanguage().getOptions().size();
            LanguageOptionDTO defaultLangOption = null;
            int indexDefault = 0;
            for (int i = 0; i < languageListSize; i++) {
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
                ApplicationPreferences.Instance.setPracticeLanguage(defaultLangOption.getCode());
            }
        }
        initializeLebals(signInScreenMode);
        // disable sign-in button
        setEnabledSigninButton(false);
    }

    private void initializeLebals(SignInScreenMode signInScreenMode) {
        if (signInScreenMode == SignInScreenMode.PRACTICE_MODE_SIGNIN && signinDTO != null) {
            SigninLabelsDTO signinLabelsDTO = signinDTO.getMetadata().getLabels();
            if (signinLabelsDTO != null) {
                signInButton.setText(signinLabelsDTO.getSigninButton());
                signinTitle.setText(signinLabelsDTO.getWelcomeSigninText());
                forgotPasswordButton.setText(signinLabelsDTO.getForgotPassword());
                gobackButton.setText(signinLabelsDTO.getGobackButton());
                emailLabel = signinDTO.getMetadata().getDataModels().getSignin().getProperties().getEmail().getLabel();
                passwordLabel = signinDTO.getMetadata().getDataModels().getSignin().getProperties().getPassword().getLabel();
                passwordEditText.setHint(passwordLabel);
                emailEditText.setHint(emailLabel);
            }
        } else if (signInScreenMode == SignInScreenMode.PATIENT_MODE_SIGNIN && signinPatientModeDTO != null) {
            SigninPatientModeLabelsDTO labelsDTO = signinPatientModeDTO.getMetadata().getLabels();
            if (labelsDTO != null) {
                signInButton.setText(labelsDTO.getSigninButton());
                signinTitle.setText(labelsDTO.getCarepaySigninTitle());
                forgotPasswordButton.setText(labelsDTO.getForgotPassword());
                gobackButton.setText(labelsDTO.getSiginHowCheckInGoBack());
                emailLabel = signinPatientModeDTO.getMetadata().getLoginDataModels().getLogin().getProperties().getEmail().getLabel();
                passwordLabel = signinPatientModeDTO.getMetadata().getLoginDataModels().getLogin().getProperties().getPassword().getLabel();
                passwordEditText.setHint(passwordLabel);
                emailEditText.setHint(emailLabel);
            }
        }
    }

    private void setEnabledSigninButton(boolean enabled) {
        if (!enabled) {
           signInButton.setBackground(getResources().getDrawable(R.drawable.bg_silver_overlay));
        } else {

            signInButton.setBackground(getResources().getDrawable(R.drawable.bg_green_overlay));
        }
        signInButton.setEnabled(enabled);
    }

    private void setClicables() {

        signInButton.setOnClickListener(new View.OnClickListener() {
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

    private void changeScreenMode(SignInScreenMode signInScreenMode) {
        if (signInScreenMode == SignInScreenMode.PATIENT_MODE_SIGNIN) {
            homeButton.setVisibility(View.VISIBLE);
            gobackButton.setVisibility(View.VISIBLE);
            langSpinner.setVisibility(View.GONE);
        } else {
            homeButton.setVisibility(View.GONE);
            gobackButton.setVisibility(View.GONE);
            langSpinner.setVisibility(View.VISIBLE);

        }
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
            //launchHomescreen();
            Map<String, String> queryMap = new HashMap<>();
            TransitionDTO transitionDTO;
            queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
            if (ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
                transitionDTO = signinDTO.getMetadata().getTransitions().getAuthenticate();
                WorkflowServiceHelper.getInstance().execute(transitionDTO, signinCallback, queryMap);
            } else if (ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
                transitionDTO = signinPatientModeDTO.getMetadata().getLinks().getLogin();
                queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
                WorkflowServiceHelper.getInstance().execute(transitionDTO, signinPatientModeCallback, queryMap);
            }
        }
        //launchHomescreen


        @Override
        public void onBeforeLogin() {
            SystemUtil.hideSoftKeyboard(SigninActivity.this);
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
            SystemUtil.showDialogMessage(SigninActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    public void setTypeFace() {
        setProximaNovaRegularTypeface(this, emailEditText);
        setProximaNovaRegularTypeface(this, passwordEditText);
    }

    WorkflowServiceCallback signinPatientModeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            Map<String, String> queryMap = new HashMap<>();

            Gson gson = new Gson();
            SigninPatientModeDTO signinPatientModeDTOLocal = gson.fromJson(workflowDTO.toString(), SigninPatientModeDTO.class);
            queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
            queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
            queryMap.put("patient_id", signinPatientModeDTOLocal.getPayload().getPatientModeLoginData().getPatientModeLoginDataMetadata().getPatientId());
            Map<String, String> headers = new HashMap<>();
            headers.put("transition", "false");
            TransitionDTO transitionDTO;
            transitionDTO = signinPatientModeDTO.getMetadata().getTransitions().getAction();
            WorkflowServiceHelper.getInstance().execute(transitionDTO, signinPatientModeAppointmentsCallback, queryMap, headers);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(SigninActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };

    WorkflowServiceCallback signinPatientModeAppointmentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PracticeNavigationHelper.getInstance().navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(SigninActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
}