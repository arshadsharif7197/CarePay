package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.dto.SignInDTO;
import com.carecloud.carepaylibray.signinsignup.fragments.ResetPasswordFragment;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;
import com.newrelic.agent.android.NewRelic;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by harish_revuri on 9/7/2016.
 * The fragment corresponding to SignUp screen.
 */
public class SigninFragment extends BaseFragment {

    private SignInDTO signInDTO;
    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;

    private FragmentActivityInterface listener;

    /**
     * @param shouldOpenNotifications a boolean indicating if Notification screen should be opened
     * @return a new instance of SigninFragment
     */
    public static SigninFragment newInstance(boolean shouldOpenNotifications) {
        Bundle args = new Bundle();
        args.putBoolean(CarePayConstants.OPEN_NOTIFICATIONS, shouldOpenNotifications);
        SigninFragment fragment = new SigninFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (FragmentActivityInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Attached Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signInDTO = (SignInDTO) listener.getDto();
        setEditTexts(view);
        setClickListeners(view);
        enableSignInButton(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void setClickListeners(View view) {

        signInButton = (Button) view.findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.replaceFragment(new SignupFragment(), true);
                reset();
            }
        });


        view.findViewById(R.id.changeLanguageText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage(signInDTO.getMetadata().getTransitions().getLanguage());
            }
        });

        view.findViewById(R.id.show_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (passwordEditText.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    setInputType(passwordEditText, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    setInputType(passwordEditText, InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        view.findViewById(R.id.forgotPasswordTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.replaceFragment(ResetPasswordFragment.newInstance(), true);
            }
        });

    }

    private void changeLanguage(TransitionDTO languageTransition) {
        Map<String, String> queryMap = new HashMap<>();
        Map<String, String> header = new HashMap<>();
        header.put("transition", "true");
        header.put("x-api-key", HttpConstants.getApiStartKey());
        getWorkflowServiceHelper().execute(languageTransition, signInCallback, queryMap, header);
    }

    private void signIn() {
        if (areAllFieldsValid(emailEditText.getText().toString(), passwordEditText.getText().toString())) {
            setSignInButtonClickable(false);
            unifiedSignIn(emailEditText.getText().toString(), passwordEditText.getText().toString(),
                    signInDTO.getMetadata().getTransitions().getSignIn());
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
            getApplicationPreferences().setUserId(userName);
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
            Gson gson = new Gson();
            String signInResponseString = gson.toJson(workflowDTO);
            UnifiedSignInResponse signInResponse = gson.fromJson(signInResponseString,
                    UnifiedSignInResponse.class);
            authenticate(signInResponse, signInDTO.getMetadata().getTransitions().getRefresh(),
                    signInDTO.getMetadata().getTransitions().getAuthenticate());
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            setSignInButtonClickable(true);
            if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE
                    || getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
                getWorkflowServiceHelper().setAppAuthorizationHelper(null);
            }
            listener.showErrorToast(CarePayConstants.INVALID_LOGIN_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void authenticate(UnifiedSignInResponse signInResponse, TransitionDTO refreshTransition,
                              TransitionDTO authenticateTransition) {
        UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getAuthorizationModel()
                .getCognito().getAuthenticationTokens();
        getAppAuthorizationHelper().setAuthorizationTokens(authTokens);
        getAppAuthorizationHelper().setRefreshTransition(refreshTransition);
        getWorkflowServiceHelper().setAppAuthorizationHelper(getAppAuthorizationHelper());

        Map<String, String> query = new HashMap<>();
        Map<String, String> header = new HashMap<>();

        String languageId = getApplicationPreferences().getUserLanguage();
        header.put("Accept-Language", languageId);
        getWorkflowServiceHelper().execute(authenticateTransition, signInCallback, query, header);
    }

    private WorkflowServiceCallback signInCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            setSignInButtonClickable(true);
            boolean shouldShowNotificationScreen = getArguments()
                    .getBoolean(CarePayConstants.OPEN_NOTIFICATIONS);
            getApplicationPreferences().setUserPhotoUrl(null);
            if (shouldShowNotificationScreen) {
                manageNotificationAsLandingScreen(workflowDTO.toString());
            } else {
                PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            setSignInButtonClickable(true);
            showErrorNotification(exceptionMessage);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private void manageNotificationAsLandingScreen(String workflow) {
        final Gson gson = new Gson();
        final MyHealthDto landingDto = gson.fromJson(workflow, MyHealthDto.class);

        showProgressDialog();
        TransitionDTO transition = landingDto.getMetadata().getLinks().getNotifications();
        getWorkflowServiceHelper().execute(transition, new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                NotificationsDTO notificationsDTO = gson
                        .fromJson(workflowDTO.toString(), NotificationsDTO.class);
                notificationsDTO.getPayload().setPracticePatientIds(landingDto
                        .getPayload().getPracticePatientIds());

                notificationsDTO.getMetadata().getLinks().setPatientBalances(landingDto.getMetadata()
                        .getLinks().getPatientBalances());
                notificationsDTO.getMetadata().getTransitions().setLogout(landingDto.getMetadata()
                        .getTransitions().getLogout());
                notificationsDTO.getMetadata().getLinks().setProfileUpdate(landingDto.getMetadata()
                        .getLinks().getProfileUpdate());
                notificationsDTO.getMetadata().getLinks().setAppointments(landingDto.getMetadata()
                        .getLinks().getAppointments());
                notificationsDTO.getMetadata().getLinks().setNotifications(landingDto.getMetadata()
                        .getLinks().getNotifications());
                notificationsDTO.getMetadata().getLinks().setMyHealth(landingDto.getMetadata()
                        .getLinks().getMyHealth());

                WorkflowDTO notificationWorkFlow = gson.fromJson(gson.toJson(notificationsDTO),
                        WorkflowDTO.class);
                hideProgressDialog();
                PatientNavigationHelper.setAccessPaymentsBalances(false);
                Bundle args = new Bundle();
                args.putBoolean(CarePayConstants.OPEN_NOTIFICATIONS, true);
                PatientNavigationHelper.navigateToWorkflow(getContext(), notificationWorkFlow, args);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        });
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
        boolean isEmailValid = StringUtil.isValidmail(email);
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

    private void enableSignInButton(String email, String password) {
        if (StringUtil.isNullOrEmpty(email) || StringUtil.isNullOrEmpty(password)) {
            setSignInButtonEnabled(false);
        } else {
            setSignInButtonEnabled(true);
        }
    }

    private void setInputType(EditText editText, int inputType) {
        int selection = editText.getSelectionEnd();
        if (editText.getInputType() != inputType) {
            editText.setInputType(inputType);
            editText.setSelection(selection);
        }
    }


    private void setEditTexts(View view) {
        signInEmailTextInputLayout = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        passwordTextInputLayout = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);
        emailEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(signInEmailTextInputLayout, null));
        passwordEditText.setOnFocusChangeListener(SystemUtil
                .getHintFocusChangeListener(passwordTextInputLayout, null));
        setTextListeners();
        setActionListeners();
    }

    /**
     * Set the focus change and selct text listeners for the edit texts
     */
    private void setTextListeners() {
        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

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
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {

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

    private void setActionListeners() {
        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    passwordEditText.requestFocus();
                    return true;
                }
                return false;
            }
        });
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                SystemUtil.hideSoftKeyboard(getActivity());
                signIn();
                return true;
            }
        });
    }

    private void reset() {
        emailEditText.setText("");
        passwordEditText.setText("");
        signInEmailTextInputLayout.setErrorEnabled(false);
        signInEmailTextInputLayout.setError(null);
        passwordTextInputLayout.setErrorEnabled(false);
        passwordTextInputLayout.setError(null);
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
}