package com.carecloud.carepay.practice.library.signin;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionResponseModel;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepay.practice.library.signin.dtos.SignInModelDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SignInPatientModeModelDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninLabelsDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeDTO;
import com.carecloud.carepay.practice.library.signin.dtos.SigninPatientModeLabelsDTO;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import static com.carecloud.carepay.practice.library.R.id.signinButton;
import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

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

    public enum SignInScreenMode {
        PRACTICE_MODE_SIGNIN, PATIENT_MODE_SIGNIN
    }

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
    private Spinner langSpinner;
    private View showPasswordButton;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // init cognito
        SignInScreenMode signinScreenMode = SignInScreenMode.PRACTICE_MODE_SIGNIN;
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            signinDTO = getConvertedDTO(SigninDTO.class);
            if (signinDTO != null) {
                if(!HttpConstants.isUseUnifiedAuth()) {
                    getApplicationMode().setCognitoDTO(signinDTO.getPayload().getPracticeModeSignin().getCognito());
                    getAppAuthoriztionHelper();
                }
                signinScreenMode = SignInScreenMode.valueOf(signinDTO.getState().toUpperCase());
            }
        } else if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            signinPatientModeDTO = getConvertedDTO(SigninPatientModeDTO.class);
            if (signinPatientModeDTO != null && signinPatientModeDTO.getPayload() != null
                    && signinPatientModeDTO.getPayload().getPatientModeSigninData() != null
                    && signinPatientModeDTO.getPayload().getPatientModeSigninData().getCognito() != null) {
                getApplicationMode().setCognitoDTO(signinPatientModeDTO.getPayload().getPatientModeSigninData().getCognito());
                getAppAuthoriztionHelper();
                signinScreenMode = SignInScreenMode.valueOf(signinPatientModeDTO.getState().toUpperCase());
            }
        }

        setContentView(R.layout.activity_signin);
        setSystemUiVisibility();
        initViews(signinScreenMode);
        setEditTexts();
        setClickables();
//        setTypeFace();

        changeScreenMode(signinScreenMode);
        isEmptyEmail = true;
        isEmptyPassword = true;

    }

    /**
     * Initailizing the view
     */
    public void initViews(SignInScreenMode signInScreenMode) {
        signInButton = (Button) findViewById(signinButton);
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
        showPasswordButton = findViewById(R.id.show_password_button);

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
            if (defaultLangOption != null && !getApplicationPreferences().getUserLanguage().isEmpty()) {
                langSpinner.setSelection(spinnerArrayAdapter.getPosition(getApplicationPreferences().getUserLanguage().toUpperCase()));
            } else {
                langSpinner.setSelection(indexDefault);
                getApplicationPreferences().setPracticeLanguage(defaultLangOption.getCode());
            }
        }

        initializeLabels(signInScreenMode);

        // disable sign-in button
        setEnabledSigninButton(false);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if(view.length()>0 && areAllValid()){
                    signInUser();
                }
                return false;
            }
        });
    }

    private void initializeLabels(SignInScreenMode signInScreenMode) {
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

    private void setClickables() {

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
                getApplicationPreferences().setUserLanguage(languages.get(position).toLowerCase());
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

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordEditText.getInputType()!=InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    setInputType(passwordEditText, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setInputType(passwordEditText, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        }
                    }, 3 * 1000);
                }else{
                    setInputType(passwordEditText, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

    }

    private void setInputType(EditText editText, int inputType){
        int selection = editText.getSelectionEnd();
        if(editText.getInputType()!=inputType){
            editText.setInputType(inputType);
            editText.setSelection(selection);
        }
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
        Log.d(LOG_TAG, "Sign in user");
        signInButton.setClickable(false);
        String userName = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!HttpConstants.isUseUnifiedAuth()) {
            getAppAuthoriztionHelper().signIn(userName, password, cognitoActionCallback);
        }else{
            unifiedSignIn(userName, password);
        }
    }

    private void unifiedSignIn(String userName, String password){
        UnifiedSignInUser user = new UnifiedSignInUser();
        user.setEmail(userName);
        user.setPassword(password);

        UnifiedSignInDTO signInDTO = new UnifiedSignInDTO();
        signInDTO.setUser(user);

        TransitionDTO signIn = signinDTO.getMetadata().getTransitions().getSignIn();
        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();

        if(signInDTO.isValidUser()){
            Gson gson = new Gson();
            getWorkflowServiceHelper().execute(signIn, unifiedLoginCallback, gson.toJson(signInDTO), queryParams, headers);
            getAppAuthoriztionHelper().setUserAlias(userName);// This must be set after the signin call is executed
        }
    }


    private WorkflowServiceCallback unifiedLoginCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            Gson gson = new Gson();
            String signInResponseString = gson.toJson(workflowDTO);
            UnifiedSignInResponse signInResponse = gson.fromJson(signInResponseString, UnifiedSignInResponse.class);
            if(signInResponse != null) {
                UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getPracticeModeAuth().getCognito().getAuthenticationTokens();
                getAppAuthoriztionHelper().setAuthorizationTokens(authTokens);
                getWorkflowServiceHelper().setAppAuthoriztionHelper(getAppAuthoriztionHelper());
            }

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("language", getApplicationPreferences().getUserLanguage());

            TransitionDTO transitionDTO;
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
                transitionDTO = signinDTO.getMetadata().getTransitions().getAuthenticate();
                getWorkflowServiceHelper().execute(transitionDTO, selectPracticeCallback, queryMap);
            } else if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
                transitionDTO = signinPatientModeDTO.getMetadata().getLinks().getLogin();
                queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
                getWorkflowServiceHelper().execute(transitionDTO, signinPatientModeCallback, queryMap);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signInButton.setEnabled(true);
            getWorkflowServiceHelper().setAppAuthoriztionHelper(null);
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private WorkflowServiceCallback selectPracticeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeSelectionResponseModel practiceSelectionModel  = DtoHelper.getConvertedDTO(PracticeSelectionResponseModel.class, workflowDTO);

            PracticeSelectionUserPractice userPractice = findUserPractice("CareCloud Clinic", practiceSelectionModel.getPayload().getUserPracticesList());

            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("language", getApplicationPreferences().getUserLanguage());
            queryMap.put("practice_mgmt", userPractice.getPracticeMgmt());
            queryMap.put("practice_id", userPractice.getPracticeId());

            TransitionDTO transitionDTO = practiceSelectionModel.getMetadata().getTransitions().getAuthenticate();
            getWorkflowServiceHelper().execute(transitionDTO, signinCallback, queryMap);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            getWorkflowServiceHelper().setAppAuthoriztionHelper(null);
            signInButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getContext());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private WorkflowServiceCallback signinCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            signInButton.setClickable(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signInButton.setClickable(true);
            getWorkflowServiceHelper().setAppAuthoriztionHelper(null);
            SystemUtil.showDefaultFailureDialog(SigninActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback signinPatientModeAppointmentsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
            signInButton.setClickable(true);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signInButton.setClickable(true);
            SystemUtil.showDefaultFailureDialog(SigninActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback signinPatientModeCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            Map<String, String> queryMap = new HashMap<>();

            Gson gson = new Gson();
            SigninPatientModeDTO signinPatientModeDTOLocal = gson.fromJson(workflowDTO.toString(), SigninPatientModeDTO.class);
            queryMap.put("language", getApplicationPreferences().getUserLanguage());
            queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
            queryMap.put("patient_id", signinPatientModeDTOLocal.getPayload().getPatientModeLoginData().getPatientModeLoginDataMetadata().getPatientId());
            getApplicationMode().setPatientId(signinPatientModeDTOLocal.getPayload().getPatientModeLoginData().getPatientModeLoginDataMetadata().getPatientId());
            Map<String, String> headers = new HashMap<>();
            headers.put("transition", "false");
            TransitionDTO transitionDTO;
            transitionDTO = signinPatientModeDTO.getMetadata().getTransitions().getAction();
            getWorkflowServiceHelper().execute(transitionDTO, signinPatientModeAppointmentsCallback, queryMap, headers);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signInButton.setClickable(true);
            SystemUtil.showDefaultFailureDialog(SigninActivity.this);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private CognitoActionCallback cognitoActionCallback = new CognitoActionCallback() {
        @Override
        public void onLoginSuccess() {
            //launchHomescreen();
            Map<String, String> queryMap = new HashMap<>();
            TransitionDTO transitionDTO;
            queryMap.put("language", getApplicationPreferences().getUserLanguage());
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
                transitionDTO = signinDTO.getMetadata().getTransitions().getAuthenticate();
                getWorkflowServiceHelper().execute(transitionDTO, signinCallback, queryMap);
            } else if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
                transitionDTO = signinPatientModeDTO.getMetadata().getLinks().getLogin();
                queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
                getWorkflowServiceHelper().execute(transitionDTO, signinPatientModeCallback, queryMap);
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
        getAppAuthoriztionHelper().getPool().getUser().signOut();
        getAppAuthoriztionHelper().setUser(null);
        if (!(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)){
            getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }

    }

    private PracticeSelectionUserPractice findUserPractice(String practiceName, List<PracticeSelectionUserPractice> practiceList){
        for(PracticeSelectionUserPractice practice : practiceList){
            if(practice.getPracticeName().equals(practiceName)){
                return practice;
            }
        }
        return null;
    }

}
