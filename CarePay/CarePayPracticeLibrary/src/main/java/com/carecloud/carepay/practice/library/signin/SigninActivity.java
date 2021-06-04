package com.carecloud.carepay.practice.library.signin;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.base.PracticeNavigationHelper;
import com.carecloud.carepay.practice.library.checkin.PatientModeCheckInCheckOutActivity;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerLanguage;
import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionDTO;
import com.carecloud.carepay.practice.library.signin.fragments.ChoosePracticeFragment;
import com.carecloud.carepay.practice.library.signin.fragments.ChoosePracticeLocationFragment;
import com.carecloud.carepay.practice.library.signin.fragments.ChoosePracticeManagementFragment;
import com.carecloud.carepay.practice.library.signin.interfaces.SelectPracticeCallback;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.models.SessionTimeInfo;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.demographics.fragments.ConfirmDialogFragment;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.ResetPasswordViewModel;
import com.carecloud.carepaylibray.signinsignup.dto.Partners;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.signinsignup.fragments.ResetPasswordFragment;
import com.carecloud.carepaylibray.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
public class SigninActivity extends BasePracticeActivity implements SelectPracticeCallback,
        ConfirmationCallback, FragmentActivityInterface {

    private static final int RESET_PASSWORD = 100;
    private ApplicationMode.ApplicationType appType;
    private TextView tvPartnerBtn;
    private LinearLayout partnerBtnLayout;
    private SignInScreenMode signinScreenMode;
    private CheckBox cbPracticeManagement;
    private String practiceManagementTitle;

    private enum SignInScreenMode {
        PRACTICE_MODE_SIGNIN, PATIENT_MODE_SIGNIN
    }

    private SignInPracticeViewModel viewModel;
    private TextView forgotPasswordTextView;
    private CarePayButton goBackButton;
    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView homeButton;
    private Button signInButton;
    private TextView languageSwitch;
    private View showPasswordButton;
    private PracticeSelectionDTO practiceSelectionModel;
    private SignInDTO signinDTO;
    private String practiceManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        signinDTO = getConvertedDTO(SignInDTO.class);
        appType = getApplicationMode().getApplicationType();
        signinScreenMode = SignInScreenMode.valueOf(signinDTO.getState().toUpperCase());
        practiceManagement = getApplicationPreferences().getStartPracticeManagement();
        practiceManagementTitle = getApplicationPreferences().getPracticeManagementTitle();

        initView();
    }

    private void initView() {
        setContentView(R.layout.activity_signin);
        setSystemUiVisibility();
        initViews(signinScreenMode);
        setEditTexts();
        setClickListeners();
        changeScreenMode(signinScreenMode);
        setUpViewModel();
        manageCrashMessage();
    }

    private boolean isMorePartnerEnable() {
        int enablePartners = 0;
        for (Partners partners : signinDTO.getPayload().getPmsPartners()) {
            if (partners.getImplemented()) {
                enablePartners++;
            }
        }
        return enablePartners > 1;
    }

    private void manageCrashMessage() {
        Bundle extra = getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO);
        if (extra != null) {
            boolean crash = extra.getBoolean(CarePayConstants.CRASH, false);
            if (crash) {
                Toast.makeText(this, Label.getLabel("crash_handled_error_message"), Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void setUpViewModel() {
        ResetPasswordViewModel resetPasswordViewModel = ViewModelProviders.of(this).get(ResetPasswordViewModel.class);
        resetPasswordViewModel.setResetPasswordTransition(signinDTO.getMetadata().getTransitions().getForgotPassword());
        setBasicObservers(resetPasswordViewModel);

        viewModel = ViewModelProviders.of(this).get(SignInPracticeViewModel.class);
        setBasicObservers(viewModel);
        viewModel.getSignInDtoObservable().observe(this, unifiedSignInResponse -> {
            TransitionDTO transition = null;
            TransitionDTO refreshTransition = null;
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
                transition = unifiedSignInResponse.getMetadata().getTransitions().getAuthenticate();
                refreshTransition = signinDTO.getMetadata().getTransitions().getRefresh();
            } else if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType
                    .PRACTICE_PATIENT_MODE) {
                if (!unifiedSignInResponse.getPayload().getSignIn().isPayload()
                        || StringUtil.isNullOrEmpty(unifiedSignInResponse.getPayload().getSignIn().getMetadata().getPatientId())) {
                    showErrorToast(unifiedSignInResponse.getPayload().getSignIn().getMetadata().getMessage());
                    setSignInButtonClickable(true);
                    return;
                }
                transition = signinDTO.getMetadata().getTransitions().getAction();
            }
            managePracticeOrAuthenticate(unifiedSignInResponse, transition, refreshTransition);
        });

        viewModel.getPracticesInfoDtoObservable().observe(this, practiceInfoDto -> {
            practiceSelectionModel = practiceInfoDto;
            List<UserPracticeDTO> practiceList = practiceSelectionModel.getPayload()
                    .getUserPracticesList();
            if (practiceList.isEmpty()) {
                onFailure("No Practice associated to this user");
                return;
            }

            if (practiceList.size() == 1) {
                UserPracticeDTO selectedPractice = practiceList.get(0);
                if (selectedPractice.getLocations().size() == 0) {
                    onFailure("No locations associated to this practice");
                    return;
                } else if (selectedPractice.getLocations().size() == 1) {
                    viewModel.authenticate(selectedPractice, selectedPractice.getLocations().get(0));
                } else {
                    ApplicationPreferences.getInstance().setPracticeId(selectedPractice.getPracticeId());
                    ChoosePracticeLocationFragment fragment = ChoosePracticeLocationFragment.newInstance(selectedPractice);
                    fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
                }
                identifyPracticeUser(selectedPractice.getUserId());
            } else {
                showPracticeSearchFragment();
            }
            setSignInButtonClickable(true);
        });

        viewModel.getAuthenticateDtoObservable().observe(this, workflowDTO -> {
            setSignInButtonClickable(true);
            getApplicationPreferences().setAppointmentCounts(null);
            if (workflowDTO.getPayload().has("timeouts")) {
                JsonArray practiceTimeOut = workflowDTO.getPayload().getAsJsonObject("timeouts")
                        .getAsJsonArray("practice");
                //set default timeout for practice mode.
                Gson gson = new Gson();
                Type type = new TypeToken<List<SessionTimeInfo>>() {
                }.getType();
                List<SessionTimeInfo> timeList = gson.fromJson(practiceTimeOut, type);
                getApplicationPreferences().setPracticeSessionTime(getDefaultSession(timeList));

                //set default timeout for Patient Mode.
                practiceTimeOut = workflowDTO.getPayload().getAsJsonObject("timeouts")
                        .getAsJsonArray("patient");
                gson = new Gson();
                type = new TypeToken<List<SessionTimeInfo>>() {
                }.getType();
                timeList = gson.fromJson(practiceTimeOut, type);
                getApplicationPreferences().setPatientSessionTime(getDefaultSession(timeList));
            }
            navigateToWorkFlow(workflowDTO);
        });

        viewModel.getSignInButtonStatus().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEnable) {
                setSignInButtonClickable(isEnable);
            }
        });
    }

    private void onFailure(String errorMessage) {
        setSignInButtonClickable(true);
        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE
                || getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
            getWorkflowServiceHelper().setAppAuthorizationHelper(null);
        }
        showErrorToast(errorMessage);
        Log.e(getString(R.string.alert_title_server_error), errorMessage);
    }

    private String getDefaultSession(List<SessionTimeInfo> timeList) {
        for (SessionTimeInfo sessionTimeInfo : timeList) {
            if (sessionTimeInfo.isDefault()) {
                return sessionTimeInfo.getName();
            }
        }
        return "2";
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
        partnerBtnLayout = findViewById(R.id.partner_btn_layout);
        tvPartnerBtn = findViewById(R.id.tv_partner_btn);
        cbPracticeManagement = findViewById(R.id.cb_practice_management);


        setUpLanguageSpinner();
        if (signInScreenMode == SignInScreenMode.PRACTICE_MODE_SIGNIN) {
            displayVersionNumber();
        } else if (signInScreenMode == SignInScreenMode.PATIENT_MODE_SIGNIN) {
            TextView signInTitle = findViewById(R.id.signinTitleTextview);
            signInTitle.setText(Label.getLabel("carepay_signin_title"));
        }

        passwordEditText.setOnEditorActionListener((view, actionId, event) -> {
            if (view.length() > 0 && areAllFieldsValid(emailEditText.getText().toString(),
                    passwordEditText.getText().toString())) {
                signIn();
            }
            return false;
        });
    }

    private void setUpLanguageSpinner() {
        languageSwitch = findViewById(R.id.languageSpinner);
        final PopupPickerLanguage popupPickerLanguage = new PopupPickerLanguage(getContext(), false,
                signinDTO.getPayload().getLanguages(), language -> changeLanguage(signinDTO.getMetadata().getLinks().getLanguage(),
                language.getCode().toLowerCase(), getWorkflowServiceHelper().getApplicationStartHeaders()));
        languageSwitch.setOnClickListener(popupPickerLanguage::showAsDropDown);
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
                if (!isEmptyEmail) { // clearAll the error
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
        homeButton.setOnClickListener(view -> onBackPressed());
        signInButton.setOnClickListener(view -> signIn());

        forgotPasswordTextView.setOnClickListener(view -> {
            replaceFragment(ResetPasswordFragment.newInstance(), true);
//            Intent intent = new Intent(SigninActivity.this, ResetPasswordActivity.class);
//            Bundle bundle = new Bundle();
//            DtoHelper.bundleDto(bundle, signinDTO);
//            intent.putExtras(bundle);
//            startActivityForResult(intent, RESET_PASSWORD);
        });

        showPasswordButton.setOnClickListener(view -> {
            if (passwordEditText.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                setInputType(passwordEditText, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                setInputType(passwordEditText, InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        tvPartnerBtn.setOnClickListener(view -> {
            showPracticeManagementFragment();
        });

        partnerBtnLayout.setVisibility(appType == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE ?
                View.GONE : View.VISIBLE);
        if (practiceManagementTitle == null) {
            tvPartnerBtn.setText(signinDTO.getPayload().getPmsPartners().get(0).getLabel());
            cbPracticeManagement.setChecked(false);
        } else {
            tvPartnerBtn.setText(practiceManagementTitle);
            cbPracticeManagement.setChecked(true);
        }

        cbPracticeManagement.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                getApplicationPreferences().setPracticeManagementTitle(practiceManagementTitle);
            } else {
                getApplicationPreferences().setPracticeManagementTitle(null);
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
            TransitionDTO signInTransition;
            signInTransition = signinDTO.getMetadata().getTransitions().getSignIn();
            setSignInButtonClickable(false);
            viewModel.unifiedSignIn(emailEditText.getText().toString(), passwordEditText.getText().toString(), signInTransition);
        }
    }

    private void managePracticeOrAuthenticate(UnifiedSignInResponse signInResponse,
                                              TransitionDTO transitionDTO,
                                              TransitionDTO refreshTransition) {
        if (signInResponse != null) {
            UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getAuthorizationModel()
                    .getCognito().getAuthenticationTokens();
            getAppAuthorizationHelper().setAuthorizationTokens(authTokens);
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
                getAppAuthorizationHelper().setRefreshTransition(refreshTransition);
            }
            getWorkflowServiceHelper().setAppAuthorizationHelper(getAppAuthorizationHelper());
        }


        if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE) {
            viewModel.getPracticesInfo(transitionDTO);
        } else if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("language", getApplicationPreferences().getUserLanguage());
            queryMap.put("practice_mgmt", getApplicationPreferences().getStartPracticeManagement());
            queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
            queryMap.put("patient_id", signInResponse.getPayload().getSignIn().getMetadata().getPatientId());
            getApplicationMode().setPatientId(signInResponse.getPayload().getSignIn().getMetadata().getPatientId());
            ApplicationPreferences.getInstance().setPatientId(signInResponse.getPayload().getSignIn()
                    .getMetadata().getPatientId());
            viewModel.authenticatePatient(transitionDTO, queryMap);
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

    View.OnClickListener goBackButtonListener = view -> onBackPressed();

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (!(getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)) {
            getApplicationMode().setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }
    }

    @Override
    public void onSelectPracticeCanceled() {
        getWorkflowServiceHelper().setAppAuthorizationHelper(null);
        setSignInButtonClickable(true);
    }

    @Override
    public void onSelectPracticeLocationCanceled(UserPracticeDTO selectedPractice) {
        showPracticeSearchFragment();
    }

    @Override
    public void onSelectPracticeManagement(Partners selectedPracticeManagement) {
        practiceManagement = selectedPracticeManagement.getPracticeMgmt();
        practiceManagementTitle = selectedPracticeManagement.getLabel();
        getApplicationPreferences().setStartPracticeManagement(practiceManagement);
        tvPartnerBtn.setText(practiceManagementTitle);
        passwordEditText.setText("");
        emailEditText.setText("");
        cbPracticeManagement.setChecked(false);
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

    private void navigateToWorkFlow(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(this, workflowDTO, true, PatientModeCheckInCheckOutActivity.CONNECTIVITY_ERROR_RESULT);
        finish();
    }

    private void showPracticeSearchFragment() {
        ChoosePracticeFragment fragment = ChoosePracticeFragment.newInstance();
        fragment.show(getSupportFragmentManager(), fragment.getClass().getName());
    }

    private void showPracticeManagementFragment() {
        ChoosePracticeManagementFragment fragment = ChoosePracticeManagementFragment.newInstance(
                signinDTO.getPayload().getPmsPartners());
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
        boolean isEmailValid = true;
        if (isEmailValidationNeeded()) {
            isEmailValid = ValidationHelper.isValidEmail(email);
        }
        if (isEmptyEmail || !isEmailValid) {
            if (isEmptyEmail) {
                setEmailError(getString(R.string.signin_signup_error_empty_email));
            } else {
                setEmailError(getString(R.string.signin_signup_error_invalid_email));
            }
        }
        return !isEmptyEmail && isEmailValid;
    }

    private boolean checkPassword(String password) {
        boolean isEmptyPassword = StringUtil.isNullOrEmpty(password);

        if (isEmptyPassword) {
            String error = getString(R.string.signin_signup_error_empty_password);
            setPasswordError(error);
        }
        return !isEmptyPassword;
    }

    private boolean isEmailValidationNeeded() {
        return !practiceManagement.equalsIgnoreCase(Defs.START_PM_TALKEHR);
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
        } else if (requestCode == PatientModeCheckInCheckOutActivity.CONNECTIVITY_ERROR_RESULT &&
                resultCode == PatientModeCheckInCheckOutActivity.CONNECTIVITY_ERROR_RESULT) {
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
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        addFragment(R.id.fragmentContainer, fragment, addToBackStack);
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        replaceFragment(R.id.fragmentContainer, fragment, addToBackStack);
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
