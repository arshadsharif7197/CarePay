package com.carecloud.carepay.practice.library.signin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.adapters.LanguageAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionDTO;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepay.practice.library.signin.fragments.ChoosePracticeLocationFragment;
import com.carecloud.carepay.practice.library.signin.fragments.PracticeSearchFragment;
import com.carecloud.carepay.practice.library.signin.interfaces.SelectPracticeCallback;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.signinsignup.fragments.ResetPasswordFragment;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.Gson;
import com.newrelic.agent.android.NewRelic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the practice app
 * Cogtino SDK use for user authentication
 * On success authentication screen will navigate next screen from the transition Json
 * On failed showing the authentication failure dialog with no navigation
 */
public class SigninActivity extends BasePracticeActivity implements SelectPracticeCallback,
        ConfirmationCallback {

    private static final int RESET_PASSWORD = 100;

    private enum SignInScreenMode {
        PRACTICE_MODE_SIGNIN, PATIENT_MODE_SIGNIN
    }

    private TextView forgotPasswordTextView;
    private CarePayButton goBackButton;
    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView homeButton;
    private Button signInButton;
    private SignInDTO signinDTO;
    private PracticeSelectionDTO practiceSelectionModel;
    private TextView languageSwitch;
    private View showPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        signinDTO = getConvertedDTO(SignInDTO.class);
        SignInScreenMode signinScreenMode = SignInScreenMode.valueOf(signinDTO.getState().toUpperCase());
        setContentView(R.layout.activity_signin);
        setSystemUiVisibility();
        initViews(signinScreenMode);
        setEditTexts();
        setClickListeners();
        changeScreenMode(signinScreenMode);

        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        if (extra != null) {
            boolean crash = extra.getBoolean(CarePayConstants.CRASH, false);
            if (crash) {
                Toast.makeText(this, Label.getLabel("crash_handled_error_message"), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getApplicationPreferences().mustForceUpdate() &&
                getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            ConfirmDialogFragment fragment = ConfirmDialogFragment
                    .newInstance(Label.getLabel("notifications.custom.forceUpdate.title"),
                            Label.getLabel("notifications.custom.forceUpdate.message.android"),
                            Label.getLabel("notifications.custom.forceUpdate.action"),
                            false,
                            R.layout.fragment_alert_dialog_single_action);
            fragment.setCallback(this);
            displayDialogFragment(fragment, false);
        }
    }

    /**
     * Initailizing the view
     */
    private void initViews(SignInScreenMode signInScreenMode) {
        signInButton = findViewById(R.id.signinButton);
        homeButton = findViewById(R.id.signInHome);
        goBackButton = findViewById(R.id.goBackButton);
        forgotPasswordTextView = findViewById(R.id.forgot_passwordTextview);
        passwordEditText = findViewById(R.id.passwordpracticeEditText);
        emailEditText = findViewById(R.id.signinEmailpracticeEditText);
        signInEmailTextInputLayout = findViewById(R.id.signInEmailTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
        showPasswordButton = findViewById(R.id.show_password_button);

        setUpLanguageSpinner();
        if (signInScreenMode == SignInScreenMode.PRACTICE_MODE_SIGNIN) {
            displayVersionNumber();
        } else if (signInScreenMode == SignInScreenMode.PATIENT_MODE_SIGNIN) {
            TextView signInTitle = findViewById(R.id.signinTitleTextview);
            signInTitle.setText(Label.getLabel("carepay_signin_title"));
        }

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (view.length() > 0 && areAllFieldsValid(emailEditText.getText().toString(),
                        passwordEditText.getText().toString())) {
                    signIn();
                }
                return false;
            }
        });
    }

    private void setUpLanguageSpinner() {
        languageSwitch = findViewById(R.id.languageSpinner);
        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), false,
                signinDTO.getPayload().getLanguages(), new LanguageAdapter.LanguageInterface() {
            @Override
            public void onLanguageSelected(OptionDTO language) {
                changeLanguage(signinDTO.getMetadata().getLinks().getLanguage(),
                        language.getCode().toLowerCase(), getWorkflowServiceHelper().getApplicationStartHeaders());
            }
        });
        languageSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupPickerLanguage.showAsDropDown(view);
            }
        });
        languageSwitch.setText(getApplicationPreferences().getUserLanguage().toUpperCase());
        final Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        headers.put("username", getApplicationPreferences().getUserName());
        headers.put("username_patient", getApplicationPreferences().getPatientId());
    }

    private void displayVersionNumber() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView versionNumber = findViewById(R.id.version_number);
            versionNumber.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException nne) {
            nne.printStackTrace();
        }
    }

    private void setEditTexts() {
        setTextListeners();
        emailEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(signInEmailTextInputLayout, null));
        passwordEditText.setOnFocusChangeListener(SystemUtil.getHintFocusChangeListener(passwordTextInputLayout, null));
    }

    private void setTextListeners() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean isEmptyEmail = StringUtil.isNullOrEmpty(emailEditText.getText().toString());
                if (!isEmptyEmail) { // clear the error
                    signInEmailTextInputLayout.setError(null);
                    signInEmailTextInputLayout.setErrorEnabled(false);
                }
                enableSignInButton(editable.toString(), passwordEditText.getText().toString());
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean isEmptyPassword = StringUtil.isNullOrEmpty(passwordEditText.getText().toString());
                if (isEmptyPassword) {
                    passwordTextInputLayout.setError(null);
                    passwordTextInputLayout.setErrorEnabled(false);
                }
                enableSignInButton(emailEditText.getText().toString(), editable.toString());
            }
        });
    }

    private void setClickListeners() {
        goBackButton.setOnClickListener(goBackButtonListener);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SigninActivity.this, ResetPasswordActivity.class);
                Bundle bundle = new Bundle();
                DtoHelper.bundleDto(bundle, signinDTO);
                intent.putExtras(bundle);
                startActivityForResult(intent, RESET_PASSWORD);
            }
        });

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEditText.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    setInputType(passwordEditText, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    setInputType(passwordEditText, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });
    }

    private void changeScreenMode(SignInScreenMode signInScreenMode) {
        if (signInScreenMode == SignInScreenMode.PATIENT_MODE_SIGNIN) {
            homeButton.setVisibility(View.VISIBLE);
            goBackButton.setVisibility(View.VISIBLE);
            languageSwitch.setVisibility(View.GONE);
            forgotPasswordTextView.setVisibility(View.VISIBLE);
            setNavigationBarVisibility();
        }
    }

    private void signIn() {
        if (areAllFieldsValid(emailEditText.getText().toString(), passwordEditText.getText().toString())) {
            TransitionDTO signInTransition = null;
            signInTransition = signinDTO.getMetadata().getTransitions().getSignIn();
            setSignInButtonClickable(false);
            unifiedSignIn(emailEditText.getText().toString(), passwordEditText.getText().toString(), signInTransition);
        }
    }

    private void unifiedSignIn(String userName, String password, TransitionDTO signInTransition) {
        UnifiedSignInUser user = new UnifiedSignInUser();
        user.setEmail(userName);
        user.setPassword(password);
        user.setDeviceToken(((AndroidPlatform) Platform.get()).openDefaultSharedPreferences()
                .getString(CarePayConstants.FCM_TOKEN, null));

        UnifiedSignInDTO signInDTO = new UnifiedSignInDTO();
        signInDTO.setUser(user);

        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();
        if (signInDTO.isValidUser()) {
            Gson gson = new Gson();
            getWorkflowServiceHelper().execute(signInTransition, unifiedLoginCallback, gson.toJson(signInDTO),
                    queryParams, headers);
            getAppAuthorizationHelper().setUser(userName);
            NewRelic.setUserId(userName);
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
            TransitionDTO transition = null;
            TransitionDTO refreshTransition = null;
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
                transition = signinDTO.getMetadata().getTransitions().getAuthenticate();
                refreshTransition = signinDTO.getMetadata().getTransitions().getRefresh();
            } else if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType
                    .PRACTICE_PATIENT_MODE) {
                transition = signinDTO.getMetadata().getTransitions().getAction();
                if (!signInResponse.getPayload().getSignIn().isPayload()
                        || StringUtil.isNullOrEmpty(signInResponse.getPayload().getSignIn().getMetadata().getPatientId())) {
                    showErrorToast(signInResponse.getPayload().getSignIn().getMetadata().getMessage());
                    setSignInButtonClickable(true);
                    return;
                }
            }
            managePracticeOrAuthenticate(signInResponse, transition, refreshTransition);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            setSignInButtonClickable(true);
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE
                    || getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
                getWorkflowServiceHelper().setAppAuthorizationHelper(null);
            }
            showErrorToast(CarePayConstants.INVALID_LOGIN_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void managePracticeOrAuthenticate(UnifiedSignInResponse signInResponse, TransitionDTO transitionDTO,
                                              TransitionDTO refreshTransition) {
        if (signInResponse != null) {
            UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getAuthorizationModel().getCognito().getAuthenticationTokens();
            getAppAuthorizationHelper().setAuthorizationTokens(authTokens);
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
                getAppAuthorizationHelper().setRefreshTransition(refreshTransition);
            }
            getWorkflowServiceHelper().setAppAuthorizationHelper(getAppAuthorizationHelper());
        }

        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());

        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            getWorkflowServiceHelper().execute(transitionDTO, selectPracticeCallback, queryMap);
        } else if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
            queryMap.put("patient_id", signInResponse.getPayload().getSignIn().getMetadata().getPatientId());
            getApplicationMode().setPatientId(signInResponse.getPayload().getSignIn().getMetadata().getPatientId());
            ApplicationPreferences.getInstance().setPatientId(signInResponse.getPayload().getSignIn()
                    .getMetadata().getPatientId());
            getWorkflowServiceHelper().execute(transitionDTO, signInCallback, queryMap);

            identifyPatientUser(signInResponse.getPayload().getSignIn().getMetadata().getUserId());
        }
    }

    private void setInputType(EditText editText, int inputType) {
        int selection = editText.getSelectionEnd();
        if (editText.getInputType() != inputType) {
            editText.setInputType(inputType);
            editText.setSelection(selection);
        }
    }

    private void enableSignInButton(String email, String password) {
        if (StringUtil.isNullOrEmpty(email) || StringUtil.isNullOrEmpty(password)) {
            setSignInButtonEnabled(false);
        } else {
            setSignInButtonEnabled(true);
        }
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
        if (!(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)) {
            getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }
    }

    @Override
    public void onSelectPractice(PracticeSelectionUserPractice userPractice) {
        if (userPractice.getLocations().size() <= 1) {
            authenticate(userPractice);
        } else {
            showChoosePracticeLocationFragment(userPractice);
        }
    }

    private void authenticate(PracticeSelectionUserPractice userPractice) {
        TransitionDTO transitionDTO = practiceSelectionModel.getMetadata().getTransitions().getAuthenticate();
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("language", getApplicationPreferences().getUserLanguage());
        queryMap.put("practice_mgmt", userPractice.getPracticeMgmt());
        queryMap.put("practice_id", userPractice.getPracticeId());
        getWorkflowServiceHelper().execute(transitionDTO, signInCallback, queryMap);
        identifyPracticeUser(userPractice.getUserId());
    }

    @Override
    public void onSelectPracticeLocation(PracticeSelectionUserPractice selectedPractice, LocationDTO selectedLocation) {
        ApplicationPreferences.getInstance().setPracticeId(selectedPractice.getPracticeId());
        ApplicationPreferences.getInstance().setPracticeLocationId(selectedLocation.getId());
        Set<String> locationIds = new HashSet<>();
        locationIds.add(String.valueOf(selectedLocation.getId()));
        ApplicationPreferences.getInstance()
                .setSelectedLocationsId(selectedPractice.getPracticeId(),
                        selectedPractice.getUserId(), locationIds);
        authenticate(selectedPractice);
    }

    @Override
    public void onSelectPracticeLocation(WorkflowDTO workflowDTO, PracticeSelectionUserPractice selectedPractice, LocationDTO selectedLocation) {
        ApplicationPreferences.getInstance().setPracticeLocationId(selectedLocation.getId());
        Set<String> locationIds = new HashSet<>();
        locationIds.add(String.valueOf(selectedLocation.getId()));
        ApplicationPreferences.getInstance()
                .setSelectedLocationsId(selectedPractice.getPracticeId(),
                        selectedPractice.getUserId(), locationIds);
        navigateToWorkFlow(workflowDTO);
        setSignInButtonClickable(true);
        identifyPracticeUser(selectedPractice.getUserId());

    }

    private void showChoosePracticeLocationFragment(PracticeSelectionUserPractice userPractice) {
        ChoosePracticeLocationFragment fragment = ChoosePracticeLocationFragment.newInstance(userPractice, null);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    @Override
    public void onSelectPracticeCanceled() {
        getWorkflowServiceHelper().setAppAuthorizationHelper(null);
        setSignInButtonClickable(true);
    }

    @Override
    public void onSelectPracticeLocationCanceled(PracticeSelectionUserPractice selectedPractice) {
        showPracticeSearchFragment(selectedPractice);
    }

    public void requestPasswordFocus() {
        passwordEditText.requestFocus();
    }

    public void requestEmailFocus() {
        emailEditText.requestFocus();
    }

    public void setPasswordError(String error) {
        passwordTextInputLayout.setErrorEnabled(true);
        passwordTextInputLayout.setError(error);
    }

    public void setEmailError(String error) {
        signInEmailTextInputLayout.setErrorEnabled(true);
        signInEmailTextInputLayout.setError(error);
    }

    public void setSignInButtonClickable(boolean clickable) {
        signInButton.setClickable(clickable);
    }

    public void setSignInButtonEnabled(boolean enable) {
        signInButton.setEnabled(enable);
    }


    private WorkflowServiceCallback selectPracticeCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            practiceSelectionModel = DtoHelper
                    .getConvertedDTO(PracticeSelectionDTO.class, workflowDTO);
            List<PracticeSelectionUserPractice> practiceList = practiceSelectionModel.getPayload()
                    .getUserPracticesList();

            if (practiceList.isEmpty()) {
                onFailure("No Practice associated to this user");
                return;
            }

            if (practiceList.size() == 1) {
                PracticeSelectionUserPractice selectedPractice = practiceList.get(0);
                if (selectedPractice.getLocations().size() < 2) {
                    navigateToWorkFlow(workflowDTO);
                    setSignInButtonClickable(true);
                    identifyPracticeUser(selectedPractice.getUserId());
                } else {
                    ApplicationPreferences.getInstance().setPracticeId(selectedPractice.getPracticeId());
                    ChoosePracticeLocationFragment fragment = ChoosePracticeLocationFragment.newInstance(selectedPractice, workflowDTO);
                    fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
                }
            } else {
                showPracticeSearchFragment(null);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            getWorkflowServiceHelper().setAppAuthorizationHelper(null);
            setSignInButtonClickable(true);
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private WorkflowServiceCallback signInCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            setSignInButtonClickable(true);
            getApplicationPreferences().setAppointmentCounts(null);
            navigateToWorkFlow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            setSignInButtonClickable(true);
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void navigateToWorkFlow(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(this, workflowDTO);
    }

    private void showPracticeSearchFragment(PracticeSelectionUserPractice selectedPractice) {
        PracticeSearchFragment fragment = PracticeSearchFragment.newInstance(selectedPractice);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    private boolean areAllFieldsValid(String email, String password) {
        boolean isPasswordValid = checkPassword(password);
        if (!isPasswordValid) {
            requestPasswordFocus();
        }
        boolean isEmailValid = checkEmail(email);
        if (!isEmailValid) {
            requestEmailFocus();
        }
        return isEmailValid && isPasswordValid;
    }

    private boolean checkEmail(String email) {
        boolean isEmptyEmail = StringUtil.isNullOrEmpty(email);
        boolean isEmailValid = ValidationHelper.isValidEmail(email);
        if (isEmptyEmail || !isEmailValid) {
            if (isEmptyEmail) {
                setEmailError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_empty_email));
            } else {
                setEmailError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_invalid_email));
            }
        }
        return !isEmptyEmail && isEmailValid;
    }

    private boolean checkPassword(String password) {
        boolean isEmptyPassword = StringUtil.isNullOrEmpty(password);

        if (isEmptyPassword) {
            String error = getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_empty_password);
            setPasswordError(error);
        }
        return !isEmptyPassword;
    }

    @Override
    public DTO getDto() {
        return practiceSelectionModel;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESET_PASSWORD && resultCode == ResetPasswordFragment.GO_TO_HOME) {
            finish();
        }
    }

    private void identifyPracticeUser(String userId) {
        MixPanelUtil.setUser(this, userId, null);
        MixPanelUtil.addCustomPeopleProperty(getString(R.string.people_is_practice_user), true);

        String[] params = {getString(R.string.param_login_type), getString(R.string.param_app_mode)};
        Object[] values = {getString(R.string.login_password), getString(R.string.app_mode_practice)};
        MixPanelUtil.logEvent(getString(R.string.event_signin_loginSuccess), params, values);
    }

    private void identifyPatientUser(String userId) {
        MixPanelUtil.setUser(this, userId, null);

        String[] params = {getString(R.string.param_login_type), getString(R.string.param_app_mode)};
        Object[] values = {getString(R.string.login_password), getString(R.string.app_mode_patient)};
        MixPanelUtil.logEvent(getString(R.string.event_signin_loginSuccess), params, values);
    }


    @Override
    public void onConfirm() {
        String appPackageName = getPackageName();
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

}
