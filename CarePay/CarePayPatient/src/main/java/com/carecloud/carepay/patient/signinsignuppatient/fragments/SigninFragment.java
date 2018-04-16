package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.myhealth.dtos.MyHealthDto;
import com.carecloud.carepay.patient.notifications.models.NotificationsDTO;
import com.carecloud.carepay.patient.selectlanguage.fragments.SelectLanguageFragment;
import com.carecloud.carepay.patient.utils.FingerprintUiHelper;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
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
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.EncryptionUtil;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.gson.Gson;
import com.newrelic.agent.android.NewRelic;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by harish_revuri on 9/7/2016.
 * The fragment corresponding to SignUp screen.
 */
public class SigninFragment extends BaseFragment {

    private static final String KEY_NAME = "carePayKey";
    private SignInDTO signInDTO;
    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private Signature mSignature;
    KeyStore mKeyStore;

    private FragmentActivityInterface callback;
    private KeyPairGenerator mKeyPairGenerator;

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
            callback = (FragmentActivityInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException("Attached Context must implement FragmentActivityInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSignature = Signature.getInstance("SHA256withECDSA");
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
            createKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
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
        signInDTO = (SignInDTO) callback.getDto();
        setEditTexts(view);
        setClickListeners(view);
        enableSignInButton(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void setClickListeners(View view) {

        signInButton = (Button) view.findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        view.findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.replaceFragment(new SignupFragment(), true);
                reset();
            }
        });


        view.findViewById(R.id.changeLanguageText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.replaceFragment(SelectLanguageFragment.newInstance(), true);
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
                callback.replaceFragment(ResetPasswordFragment.newInstance(), true);
            }
        });

        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && FingerprintUiHelper.isFingerprintAuthAvailable(fingerprintManagerCompat)
                && ApplicationPreferences.getInstance().getUserName() != null
                && !ApplicationPreferences.getInstance().getUserName().equals("-")) {
            view.findViewById(R.id.touchIdButton).setVisibility(View.VISIBLE);
            view.findViewById(R.id.touchIdButton).setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    FingerprintAuthenticationDialogFragment fragment = FingerprintAuthenticationDialogFragment.newInstance();
                    if (initSignature()) {
                        // Show the fingerprint dialog. The user has the option to use the fingerprint with
                        // crypto, or you can fall back to using a server-side verified password.
                        fragment.setCryptoObject(new FingerprintManagerCompat.CryptoObject(mSignature));
                        callback.displayDialogFragment(fragment, true);
                    }

                }
            });
        } else {
            signInButton.setLayoutParams(new RelativeLayout
                    .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

    }

    public void signIn(String email, String pwd) {
        if (areAllFieldsValid(email, pwd) && signInButton.isClickable()) {
            setSignInButtonClickable(false);
            unifiedSignIn(email, pwd, signInDTO.getMetadata().getTransitions().getSignIn());
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
            getWorkflowServiceHelper().execute(signInTransition, getUnifiedLoginCallback(userName, password), gson.toJson(signInDTO),
                    queryParams, headers);
            getAppAuthorizationHelper().setUser(userName);
            getApplicationPreferences().setUserId(userName);
            NewRelic.setUserId(userName);
        }
    }

    private WorkflowServiceCallback getUnifiedLoginCallback(final String user, final String password) {
        return new WorkflowServiceCallback() {

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
                        signInDTO.getMetadata().getTransitions().getAuthenticate(), user, password);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                setSignInButtonClickable(true);
                if (getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE
                        || getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PATIENT) {
                    getWorkflowServiceHelper().setAppAuthorizationHelper(null);
                }
                callback.showErrorToast(CarePayConstants.INVALID_LOGIN_ERROR_MESSAGE);
                Log.e(getString(R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

    private void authenticate(UnifiedSignInResponse signInResponse, TransitionDTO refreshTransition,
                              TransitionDTO authenticateTransition, String user, String password) {
        UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getAuthorizationModel()
                .getCognito().getAuthenticationTokens();
        getAppAuthorizationHelper().setAuthorizationTokens(authTokens);
        getAppAuthorizationHelper().setRefreshTransition(refreshTransition);
        getWorkflowServiceHelper().setAppAuthorizationHelper(getAppAuthorizationHelper());

        Map<String, String> query = new HashMap<>();
        Map<String, String> header = new HashMap<>();

        String languageId = getApplicationPreferences().getUserLanguage();
        header.put("Accept-Language", languageId);
        getWorkflowServiceHelper().execute(authenticateTransition, getSignInCallback(user, password), query, header);
    }

    private WorkflowServiceCallback getSignInCallback(final String user, final String password) {
        return new WorkflowServiceCallback() {

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
                getApplicationPreferences().writeObjectToSharedPreference(CarePayConstants
                        .DEMOGRAPHICS_ADDRESS_BUNDLE, null);
                if (shouldShowNotificationScreen) {
                    manageNotificationAsLandingScreen(workflowDTO.toString());
                } else {
                    PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
                }
                ApplicationPreferences.getInstance().setUserName(user);
                String encryptedPassword = EncryptionUtil.encrypt(getContext(), password, user);
                ApplicationPreferences.getInstance().setUserPassword(encryptedPassword);

                MyHealthDto myHealthDto = DtoHelper.getConvertedDTO(MyHealthDto.class, workflowDTO);
                String userId = myHealthDto.getPayload().getPracticePatientIds().get(0).getUserId();
                MixPanelUtil.setUser(getContext(), userId, myHealthDto.getPayload().getDemographicDTO());

                MixPanelUtil.logEvent(getString(R.string.event_signin_loginSuccess),
                        getString(R.string.param_login_type),
                        getString(R.string.login_password));

            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                setSignInButtonClickable(true);
                showErrorNotification(exceptionMessage);
                Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
            }
        };
    }

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
        if (isEmptyEmail) {
            setEmailError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_empty_email));
        }
        boolean isEmailValid = ValidationHelper.isValidEmail(email);
        if (!isEmailValid) {
            setEmailError(getString(com.carecloud.carepaylibrary.R.string.signin_signup_error_invalid_email));
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
                signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
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


    /**
     * Generates an asymmetric key pair in the Android Keystore. Every use of the private key must
     * be authorized by the user authenticating with fingerprint. Public key use is unrestricted.
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void createKeyPair() {
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_SIGN)
                            .setDigests(KeyProperties.DIGEST_SHA256)
                            .setAlgorithmParameterSpec(new ECGenParameterSpec("secp256r1"))
                            // Require the user to authenticate with a fingerprint to authorize
                            // every use of the private key
                            .setUserAuthenticationRequired(true)
                            .build());
            mKeyPairGenerator.generateKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            Log.e("Breeze",e.getLocalizedMessage());
        }
    }

    /**
     * Initialize the {@link Signature} instance with the created key in the
     * {@link #createKeyPair()} method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean initSignature() {
        try {
            mKeyStore.load(null);
            PrivateKey key = (PrivateKey) mKeyStore.getKey(KEY_NAME, null);
            mSignature.initSign(key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }
}