package com.carecloud.carepay.practice.library.signin;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
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
import com.carecloud.carepay.practice.library.signin.dtos.SignInModelDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SignInPatientModeModelDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninLabelsDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeLabelsDTO;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the practice app
 * Cogtino SDK use for user authentication
 * On success authentication screen will navigate next screen from the transition Json
 * On failed showing the authentication failure dialog with no navigation
 */
public class SigninActivity extends BasePracticeActivity {

    WorkflowServiceCallback signinCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            signInButton.setClickable(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            signInButton.setClickable(true);
            SystemUtil.showDefaultFailureDialog(SigninActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
    WorkflowServiceCallback signinPatientModeAppointmentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            signInButton.setClickable(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            signInButton.setClickable(true);
            SystemUtil.showDefaultFailureDialog(SigninActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };
    private TextView signinButton;
    private TextView forgotPasswordButton;
    private TextView signinTitle;
    private CarePayButton gobackButton;
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
    WorkflowServiceCallback signinPatientModeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            ProgressDialogUtil.getInstance(getContext()).show();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            Map<String, String> queryMap = new HashMap<>();

            Gson gson = new Gson();
            SigninPatientModeDTO signinPatientModeDTOLocal = gson.fromJson(workflowDTO.toString(), SigninPatientModeDTO.class);
            queryMap.put("language", ApplicationPreferences.Instance.getUserLanguage());
            queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
            queryMap.put("patient_id", signinPatientModeDTOLocal.getPayload().getPatientModeLoginData().getPatientModeLoginDataMetadata().getPatientId());
            ApplicationMode.getInstance().setPatientId(signinPatientModeDTOLocal.getPayload().getPatientModeLoginData().getPatientModeLoginDataMetadata().getPatientId());
            Map<String, String> headers = new HashMap<>();
            headers.put("transition", "false");
            TransitionDTO transitionDTO;
            transitionDTO = signinPatientModeDTO.getMetadata().getTransitions().getAction();
            WorkflowServiceHelper.getInstance().execute(transitionDTO, signinPatientModeAppointmentsCallback, queryMap, headers);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            ProgressDialogUtil.getInstance(getContext()).dismiss();
            signInButton.setClickable(true);
            SystemUtil.showDefaultFailureDialog(SigninActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

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
            signInButton.setClickable(true);
            SystemUtil.showFailureDialogMessage(SigninActivity.this,
                    "Sign-in failed",
                    "Invalid user id or password");

        }
    };
    private Spinner langSpinner;
    private List<String> modeSwitchOptions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // init cognito
        SignInScreenMode signinScreenMode = SignInScreenMode.PRACTICE_MODE_SIGNIN;
        if (ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            signinDTO = getConvertedDTO(SigninDTO.class);
            if (signinDTO != null && signinDTO.getPayload() != null && signinDTO.getPayload().getPracticeModeSignin() != null && signinDTO.getPayload().getPracticeModeSignin().getCognito() != null) {
                ApplicationMode.getInstance().setCognitoDTO(signinDTO.getPayload().getPracticeModeSignin().getCognito());
                CognitoAppHelper.init(this);
                signinScreenMode = SignInScreenMode.valueOf(signinDTO.getState().toUpperCase());
            }
        } else if (ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            signinPatientModeDTO = getConvertedDTO(SigninPatientModeDTO.class);
            if (signinPatientModeDTO != null && signinPatientModeDTO.getPayload() != null
                    && signinPatientModeDTO.getPayload().getPatientModeSigninData() != null
                    && signinPatientModeDTO.getPayload().getPatientModeSigninData().getCognito() != null) {
                ApplicationMode.getInstance().setCognitoDTO(signinPatientModeDTO.getPayload().getPatientModeSigninData().getCognito());
                CognitoAppHelper.init(getApplicationContext());
                signinScreenMode = SignInScreenMode.valueOf(signinPatientModeDTO.getState().toUpperCase());
            }
        }

        setContentView(R.layout.activity_signin);
        setSystemUiVisibility();
        initViews(signinScreenMode);
        setEditTexts();
        setClicables();
        setTypeFace();

        changeScreenMode(signinScreenMode);
        isEmptyEmail = true;
        isEmptyPassword = true;

    }

    /**
     * Initailizing the view
     */
    public void initViews(SignInScreenMode signInScreenMode) {
        signInButton = (Button) findViewById(R.id.signinButton);
        homeButton = (ImageView) findViewById(R.id.signInHome);
        gobackButton = (CarePayButton) findViewById(R.id.goBackButton);
        gobackButton.setOnClickListener(goBackButtonListener);
        forgotPasswordButton = (TextView) findViewById(R.id.forgot_passwordTextview);
        passwordEditText = (EditText) findViewById(R.id.passwordpracticeEditText);
        emailEditText = (EditText) findViewById(R.id.signinEmailpracticeEditText);
        langSpinner = (Spinner) findViewById(R.id.signinLangSpinner);
        signInEmailTextInputLayout = (TextInputLayout) findViewById(R.id.signInEmailTextInputLayout);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
        signinTitle = (TextView) findViewById(R.id.signinTitleTextview);

        if (signInScreenMode == SignInScreenMode.PRACTICE_MODE_SIGNIN
                && signinDTO.getPayload().getLanguages() != null) {

            int languageListSize = signinDTO.getPayload().getLanguages().size();
            LanguageOptionDTO defaultLangOption = null;

            int indexDefault = 0;
            for (int i = 0; i < languageListSize; i++) {
                LanguageOptionDTO languageOption = signinDTO.getPayload().getLanguages().get(i);
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

            // this should be always true, as there's always a default option
            if (defaultLangOption != null && !ApplicationPreferences.Instance.getUserLanguage().isEmpty()) {
                langSpinner.setSelection(spinnerArrayAdapter.getPosition(ApplicationPreferences.Instance.getUserLanguage().toUpperCase()));
            } else {
                langSpinner.setSelection(indexDefault);
                ApplicationPreferences.Instance.setPracticeLanguage(defaultLangOption.getCode());
            }
        }

        initializeLebals(signInScreenMode);

        // disable sign-in button
        setEnabledSigninButton(false);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initializeLebals(SignInScreenMode signInScreenMode) {
        if (signInScreenMode == SignInScreenMode.PRACTICE_MODE_SIGNIN && signinDTO != null) {
            SigninLabelsDTO signinLabelsDTO = signinDTO.getMetadata().getLabels();
            if (signinLabelsDTO != null) {
                signInButton.setText(signinLabelsDTO.getSigninButton());
                signinTitle.setText(signinLabelsDTO.getWelcomeSigninText());
                forgotPasswordButton.setText(signinLabelsDTO.getForgotPassword());
                gobackButton.setText(signinLabelsDTO.getGobackButton());

                SignInModelDTO signInModelDTO = signinDTO.getMetadata().getDataModels().getSignin();
                if (signInModelDTO != null) {
                    emailLabel =signInModelDTO.getProperties().getEmail().getLabel();
                    passwordLabel = signInModelDTO.getProperties().getPassword().getLabel();
                }
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

                SignInPatientModeModelDTO login = signinPatientModeDTO.getMetadata().getLoginDataModels().getLogin();
                if (login != null) {
                    emailLabel = login.getProperties().getEmail().getLabel();
                    passwordLabel = login.getProperties().getPassword().getLabel();
                }
                passwordEditText.setHint(passwordLabel);
                emailEditText.setHint(emailLabel);
            }
        }
    }

    private void setEnabledSigninButton(boolean enabled) {
        if (!enabled) {
            signInButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_silver_overlay));
        } else {

            signInButton.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_green_overlay));
        }
        signInButton.setEnabled(enabled);
    }

    private void setClicables() {

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areAllValid()) {
                    signInButton.setClickable(false);
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
            setNavigationBarVisibility();
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
        Log.v(this.getClass().getSimpleName(), "passw: " + password);
        CognitoAppHelper.signIn(userName, password, cognitoActionCallback);

    }

    public void setTypeFace() {
        setProximaNovaRegularTypeface(this, emailEditText);
        setProximaNovaRegularTypeface(this, passwordEditText);
    }

    public enum SignInScreenMode {
        PRACTICE_MODE_SIGNIN, PATIENT_MODE_SIGNIN
    }

    /**
     * Click listener for go back button
     */
    View.OnClickListener goBackButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onBackPressed();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // log out previous user from Cognito
        Log.v(this.getClass().getSimpleName(), "sign out Cognito");
        CognitoAppHelper.getPool().getUser().signOut();
        CognitoAppHelper.setUser(null);
        if (!(ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)){
            ApplicationMode.getInstance().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }

    }

}
