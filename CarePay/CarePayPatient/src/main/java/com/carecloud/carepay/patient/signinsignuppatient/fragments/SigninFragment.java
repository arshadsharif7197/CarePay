package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.signinsignuppatient.SigninSignupActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInDTO;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInUser;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInLablesDTO;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInSignUpDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;




/**
 * Created by harish_revuri on 9/7/2016.
 * The fragment corresponding to SignUp screen.
 */
public class SigninFragment extends BaseFragment {

    public static final String LOG_TAG = SigninFragment.class.getSimpleName();

    private SignInSignUpDTO signInSignUpDTO;
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTexInput;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView changeLanguageTextView;
    private TextView forgotPasswordTextView;
    private Button signinButton;
    private Button signupButton;
    private ProgressBar progressBar;
    private View showPasswordButton;

    private LinearLayout parentLayout;
    private boolean isEmptyEmail;
    private boolean isEmptyPassword;
    private SignInLablesDTO signInLablesDTO;
    private String languageid;

    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        languageid =getApplicationPreferences().getUserLanguage();
        signInLablesDTO = ((SigninSignupActivity) getActivity()).getSignInLablesDTO();
        signInSignUpDTO = ((SigninSignupActivity) getActivity()).getSignInSignUpDTO();
        parentLayout = (LinearLayout) view.findViewById(R.id.signin_layout);

        progressBar = (ProgressBar) view.findViewById(R.id.signInProgress);
        progressBar.setVisibility(View.INVISIBLE);
        initview(view);
        setEditTexts(view);

        setClickables(view);

        isEmptyEmail = true;
        isEmptyPassword = true;

        return view;
    }

    private void initview(View view) {
        signinButton = (Button) view.findViewById(R.id.signin_button);
        signinButton.setEnabled(false);
        signupButton = (Button) view.findViewById(R.id.signup_button);
        changeLanguageTextView = (TextView) view.findViewById(R.id.changeLanguageText);
        forgotPasswordTextView = (TextView) view.findViewById(R.id.forgotPasswordTextView);
        showPasswordButton = view.findViewById(R.id.show_password_button);
        if (signInLablesDTO != null) {
            String signinLabel = signInLablesDTO.getSigninButton();
            signinButton.setText(signinLabel);
            String createAcountLabel = signInLablesDTO.getCreateNewAccountButton();
            signupButton.setText(createAcountLabel);
            String forgotpassword = signInLablesDTO.getForgotPasswordLink();
            forgotPasswordTextView.setText(forgotpassword);
            String changeLangugae = signInLablesDTO.getChangeLanguageLink();
            changeLanguageTextView.setText(changeLangugae);
        }
    }

    private void setClickables(View view) {

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllValid()) {
                    signinButton.setEnabled(false);
                    signInUser();
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                SignupFragment fragment = (SignupFragment) fm.findFragmentByTag(SignupFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new SignupFragment();
                    fragment.setRetainInstance(true);
                }
                fm.beginTransaction()
                        .replace(R.id.layoutSigninSignup, fragment, SignupFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
                reset();
            }
        });


        changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // relaunch select language

                Map<String, String> queryMap = new HashMap<>();
                Map<String, String> header = new HashMap<>();
                header.put("transition", "true");
                header.put("x-api-key", HttpConstants.getApiStartKey());
                TransitionDTO transitionDTO = signInSignUpDTO.getMetadata().getTransitions().getLanguage();
                getWorkflowServiceHelper().execute(transitionDTO, loginCallback, queryMap, header);
            }
        });

        showPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(passwordEditText.getInputType()!=InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    setInputType(passwordEditText, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            setInputType(passwordEditText, InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
//                        }
//                    }, 3 * 1000);
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


    private void setEditTexts(View view) {
        emailTextInput = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);
        passwordTexInput = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);
        if (signInSignUpDTO != null) {
            String emailAddress = signInLablesDTO.getEmail();
            if (SystemUtil.isNotEmptyString(emailAddress)) {
                emailTextInput.setTag(emailAddress);
            }

            emailEditText.setHint(emailAddress);
            emailEditText.setTag(emailTextInput);

            String password = signInSignUpDTO.getMetadata().getDataModels().getSignin().getProperties().getPassword().getLabel();
            if (SystemUtil.isNotEmptyString(password)) {
                passwordTexInput.setTag(password);
            }

            passwordEditText.setHint(password);
            passwordEditText.setTag(passwordTexInput);
        }
        setTextListeners();
        setChangeFocusListeners();
        setActionListeners();

        emailEditText.clearFocus();
        passwordEditText.clearFocus();
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
                isEmptyEmail = StringUtil.isNullOrEmpty(emailEditText.getText().toString());
                if (!isEmptyEmail) { // clear the error
                    emailTextInput.setError(null);
                    emailTextInput.setErrorEnabled(false);
                }
                enableSigninButton();
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
                isEmptyPassword = StringUtil.isNullOrEmpty(passwordEditText.getText().toString());
                if (isEmptyPassword) {
                    passwordTexInput.setError(null);
                    passwordTexInput.setErrorEnabled(false);
                }
                enableSigninButton();
            }
        });
        emailEditText.clearFocus();
    }

    private void setChangeFocusListeners() {
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean bool) {
                if (bool) {
                    SystemUtil.showSoftKeyboard(getActivity());
                }
                SystemUtil.handleHintChange(view, bool);
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    passwordEditText.clearFocus();
                    parentLayout.requestFocus();
                    SystemUtil.hideSoftKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });
    }

    private boolean checkEmail() {
        String email = emailEditText.getText().toString();
        isEmptyEmail = StringUtil.isNullOrEmpty(email);
        boolean isEmailValid = StringUtil.isValidmail(email);
        emailTextInput.setErrorEnabled(isEmptyEmail || !isEmailValid); // enable for error if either empty or invalid email
        if (isEmptyEmail) {
            emailTextInput.setError(signInLablesDTO.getPleaseEnterEmail());
        } else if (!isEmailValid) {
            emailTextInput.setError(signInLablesDTO.getInvalidEmail());
        } else {
            emailTextInput.setError(null);
        }
        return !isEmptyEmail && isEmailValid;
    }

    private boolean checkPassword() {
        isEmptyPassword = StringUtil.isNullOrEmpty(passwordEditText.getText().toString());
        String error = (isEmptyPassword ? signInLablesDTO.getPleaseEnterPassword() : null);
        passwordTexInput.setErrorEnabled(isEmptyPassword);
        passwordTexInput.setError(error);
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

    private void enableSigninButton() {
        boolean areAllNonEmpty = !(isEmptyEmail || isEmptyPassword);
        signinButton.setEnabled(areAllNonEmpty);
    }

    private void reset() {
        emailEditText.setText("");
        passwordEditText.setText("");
        emailTextInput.setErrorEnabled(false);
        emailTextInput.setError(null);
        passwordTexInput.setErrorEnabled(false);
        passwordTexInput.setError(null);
    }

    private void signInUser() {
        Log.v(LOG_TAG, "sign in user");
        String userName = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(!HttpConstants.isUseUnifiedAuth()) {
            getAppAuthorizationHelper().signIn(userName, password, cognitoActionCallback);
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

        TransitionDTO signIn = signInSignUpDTO.getMetadata().getTransitions().getSignIn();
        Map<String, String> queryParams = new HashMap<>();
        Map<String, String> headers = getWorkflowServiceHelper().getApplicationStartHeaders();

        if(signInDTO.isValidUser()){
            Gson gson = new Gson();
            getWorkflowServiceHelper().execute(signIn, unifiedLoginCallback, gson.toJson(signInDTO), queryParams, headers);
            getAppAuthorizationHelper().setUserAlias(userName);
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
                UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getPatientAppAuth().getCognito().getAuthenticationTokens();
                getAppAuthorizationHelper().setAuthorizationTokens(authTokens);
                getWorkflowServiceHelper().setAppAuthorizationHelper(getAppAuthorizationHelper());
            }

            Map<String, String> query = new HashMap<>();
            Map<String, String> header = new HashMap<>();
            header.put("Accept-Language", languageid);
            getWorkflowServiceHelper().execute(signInSignUpDTO.getMetadata().getTransitions().getAuthenticate(), loginCallback, query, header);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signinButton.setEnabled(true);
            getWorkflowServiceHelper().setAppAuthorizationHelper(null);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };


    private WorkflowServiceCallback loginCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            signinButton.setEnabled(true);
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            signinButton.setEnabled(true);
            SystemUtil.showDefaultFailureDialog(getActivity());
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    private CognitoActionCallback cognitoActionCallback = new CognitoActionCallback() {
        @Override
        public void onLoginSuccess() {
            Map<String, String> query = new HashMap<>();
            Map<String, String> header = new HashMap<>();
            header.put("Accept-Language", languageid);
            getWorkflowServiceHelper().execute(signInSignUpDTO.getMetadata().getTransitions().getAuthenticate(), loginCallback,query,header);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onBeforeLogin() {
            SystemUtil.hideSoftKeyboard(getActivity());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoginFailure(String exceptionMessage) {
            signinButton.setEnabled(true);
            SystemUtil.showFailureDialogMessage(getContext(),
                    "Sign-in failed",
                    "Invalid user id or password");

        }
    };

}