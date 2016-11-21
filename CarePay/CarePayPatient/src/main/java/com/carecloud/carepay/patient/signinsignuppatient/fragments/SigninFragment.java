package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
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
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

import com.carecloud.carepaylibrary.R;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

import com.carecloud.carepaylibray.signinsignup.dtos.SignInLablesDTO;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInSignUpDTO;


import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;




import java.util.HashMap;
import java.util.Map;



/**
 * Created by harish_revuri on 9/7/2016.
 * The fragment corresponding to SignUp screen.
 */
public class SigninFragment extends Fragment {

    SignInSignUpDTO signInSignUpDTO;
    WorkflowServiceCallback loginCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            //   SystemUtil.showDialogMessage(SplashActivity.this, getString(R.string.alert_title_server_error), exceptionMessage);
        }
    };
    private TextInputLayout emailTextInput;
    private TextInputLayout passwordTexInput;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView changeLanguageTextView;
    private TextView forgotPasswordTextView;
    private Button signinButton;
    private Button signupButton;
    private ProgressBar progressBar;
    CognitoActionCallback cognitoActionCallback = new CognitoActionCallback() {
        @Override
        public void onLoginSuccess() {
            Map<String, String> query = new HashMap<>();
            Map<String, String> header = new HashMap<>();
            header.put("Accept-Language",langaueid);
            WorkflowServiceHelper.getInstance().execute(signInSignUpDTO.getMetadata().getTransitions().getAuthenticate(), loginCallback,query,header);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onBeforeLogin() {
            SystemUtil.hideSoftKeyboard(getActivity());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoginFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(getContext(),
                    "Sign-in failed",
                    "Invalid user id or password");

        }
    };
    private LinearLayout parentLayout;
    private boolean isEmptyEmail;
    private boolean isEmptyPassword;
    private SignInLablesDTO signInLablesDTO;
    String langaueid;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
       langaueid=ApplicationPreferences.Instance.getUserLanguage();
        signInLablesDTO = ((SigninSignupActivity) getActivity()).getSignInLablesDTO();
        signInSignUpDTO = ((SigninSignupActivity) getActivity()).getSignInSignUpDTO();
        parentLayout = (LinearLayout) view.findViewById(R.id.signin_layout);

        progressBar = (ProgressBar) view.findViewById(R.id.signInProgress);
        progressBar.setVisibility(View.INVISIBLE);
        initview(view);
        setEditTexts(view);

        setClickbles(view);

        setTypefaces();

        isEmptyEmail = true;
        isEmptyPassword = true;

        emailEditText.setText("aa29@cc.com");
        passwordEditText.setText("Test123!");

        return view;
    }

    private void initview(View view) {
        signinButton = (Button) view.findViewById(R.id.signin_button);
        signinButton.setEnabled(false);
        signupButton = (Button) view.findViewById(R.id.signup_button);
        changeLanguageTextView = (TextView) view.findViewById(R.id.changeLanguageText);
        forgotPasswordTextView = (TextView) view.findViewById(R.id.forgotPasswordTextView);
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

    private void setClickbles(View view) {

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (areAllValid()) {
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
                WorkflowServiceHelper.getInstance().execute(transitionDTO, loginCallback, queryMap, header);
            }
        });
    }

    private void setTypefaces() {
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), emailEditText);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), passwordEditText);

        SystemUtil.setProximaNovaSemiboldTextInputLayout(getActivity(), emailTextInput);
        SystemUtil.setProximaNovaSemiboldTextInputLayout(getActivity(), passwordTexInput);

        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), signinButton);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), signupButton);

        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), changeLanguageTextView);
        SystemUtil.setProximaNovaSemiboldTypeface(getActivity(), forgotPasswordTextView);
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

    // cognito
    private void signInUser() {
        Log.v(LOG_TAG, "sign in user");
        String userName = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        CognitoAppHelper.signIn(userName, password, cognitoActionCallback);

    }

    /*private void getDemographicInformation() {
        progressBar.setVisibility(View.VISIBLE);
        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicDTO> call = apptService.fetchDemographics();
        call.enqueue(new Callback<DemographicDTO>() {
            @Override
            public void onResponse(Call<DemographicDTO> call, Response<DemographicDTO> response) {
                DemographicDTO demographicDTO = response.body();
                progressBar.setVisibility(View.GONE);
                Log.v(LOG_TAG, "demographic info fetched");
                launchAppointments(demographicDTO);
            }

            @Override
            public void onFailure(Call<DemographicDTO> call, Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                Log.e(LOG_TAG, "failed fetching demogr info", throwable);
            }
        });
    }

    private void launchAppointments(DemographicDTO demographicDTO) {
        // do to Demographics
        Intent intent = new Intent(getActivity(), AppointmentsActivity.class);
        // pass the object into the gson
        Gson gson = new Gson();
        intent.putExtra("demographics_model", gson.toJson(demographicDTO, DemographicDTO.class));

        startActivity(intent);
        getActivity().finish();
    }*/
}