package com.carecloud.carepay.patient.signinsignuppatient.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.selectlanguage.fragments.SelectLanguageFragment;
import com.carecloud.carepay.patient.signinsignuppatient.SignInViewModel;
import com.carecloud.carepay.patient.utils.FingerprintUiHelper;
import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.base.BaseFragment;
import com.carecloud.carepaylibray.base.NavigationStateConstants;
import com.carecloud.carepaylibray.base.PlainWebViewFragment;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;
import com.carecloud.carepaylibray.signinsignup.fragments.ResetPasswordFragment;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.carecloud.carepaylibray.utils.ValidationHelper;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.Signature;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.ECGenParameterSpec;

/**
 * Created by harish_revuri on 9/7/2016.
 * The fragment corresponding to SignUp screen.
 */
public class SigninFragment extends BaseFragment {

    private static final String KEY_NAME = "carePayKey";
    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private Signature mSignature;
    private KeyStore mKeyStore;

    private FragmentActivityInterface callback;
    private KeyPairGenerator mKeyPairGenerator;
    private SignInViewModel viewModel;

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
    public void onAttach(@NonNull Context context) {
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
            mKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC,
                    "AndroidKeyStore");
            createKeyPair();
        } catch (NoSuchAlgorithmException | KeyStoreException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        setUpViewModel();
    }

    private void setUpViewModel() {
        viewModel = new ViewModelProvider(getActivity()).get(SignInViewModel.class);
        viewModel.getSignInResultNavigatorObservable().observe(this, workflowDTO -> {
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO, getActivity()
                    .getIntent().getExtras());
        });
        viewModel.getSignInResultObservable().observe(this, response -> {
            if (response.equals(SignInViewModel.SHOW_CHOOSE_PROFILE_SCREEN)) {
                setSignInButtonClickable(true);
                callback.addFragment(ChooseProfileFragment.newInstance(emailEditText.getText().toString(),
                        passwordEditText.getText().toString()), true);
            } else if (response.equals(SignInViewModel.SIGN_IN_ERROR)) {
                setSignInButtonClickable(true);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEditTexts(view);
        setClickListeners(view);
        enableSignInButton(emailEditText.getText().toString(), passwordEditText.getText().toString());
    }

    private void setClickListeners(View view) {

        signInButton = view.findViewById(R.id.signin_button);
        signInButton.setOnClickListener(view1 -> signIn(emailEditText.getText().toString(),
                passwordEditText.getText().toString()));

        view.findViewById(R.id.changeLanguageText).setOnClickListener(view13
                -> callback.replaceFragment(SelectLanguageFragment.newInstance(), true));

        view.findViewById(R.id.show_password_button).setOnClickListener(view14 -> {
            if (passwordEditText.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                setInputType(passwordEditText, InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                setInputType(passwordEditText, InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        });

        view.findViewById(R.id.forgotPasswordTextView).setOnClickListener(view15
                -> callback.replaceFragment(ResetPasswordFragment.newInstance(), true));

        setUpFingerPrint(view);

        view.findViewById(R.id.get_started).setOnClickListener(view12 -> {
            PlainWebViewFragment fragment = PlainWebViewFragment
                    .newInstance(HttpConstants.getRetailUrl() + CarePayConstants.GET_STARTED_URL);
            callback.replaceFragment(fragment, true);
        });
    }

    private void setUpFingerPrint(View view) {
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && FingerprintUiHelper.isFingerprintAuthAvailable(fingerprintManagerCompat)
                && !StringUtil.isNullOrEmpty(ApplicationPreferences.getInstance().getUserName())
                && !ApplicationPreferences.getInstance().getUserName().equals("-")) {
            view.findViewById(R.id.touchIdButton).setVisibility(View.VISIBLE);
            view.findViewById(R.id.touchIdButton).setOnClickListener(v -> {
                FingerprintAuthenticationDialogFragment fragment = FingerprintAuthenticationDialogFragment
                        .newInstance();
                if (initSignature()) {
                    // Show the fingerprint dialog. The user has the option to use the fingerprint with
                    // crypto, or you can fall back to using a server-side verified password.
                    fragment.setCryptoObject(new FingerprintManagerCompat.CryptoObject(mSignature));
                    callback.displayDialogFragment(fragment, true);
                }

            });
        }
    }

    public void signIn(String email, String pwd) {
        if (areAllFieldsValid(email, pwd) && signInButton.isClickable()) {
            setSignInButtonClickable(false);
            String inviteId = null;
            if (getActivity().getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO) != null) {
                inviteId = getActivity().getIntent().getBundleExtra(NavigationStateConstants.EXTRA_INFO)
                        .getString("inviteId");
            }
            boolean openNotificationScreen = getArguments().getBoolean(CarePayConstants.OPEN_NOTIFICATIONS);
            viewModel.signInStep1(email, pwd, inviteId, openNotificationScreen);
        }
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
            setEmailError(getString(R.string.signin_signup_error_empty_email));
        }
        boolean isEmailValid = ValidationHelper.isValidEmail(email);
        if (!isEmailValid) {
            setEmailError(getString(R.string.signin_signup_error_invalid_email));
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
        signInEmailTextInputLayout = view.findViewById(R.id.signInEmailTextInputLayout);
        emailEditText = view.findViewById(R.id.signinEmailEditText);
        passwordEditText = view.findViewById(R.id.passwordEditText);
        passwordTextInputLayout = view.findViewById(R.id.passwordTextInputLayout);
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
        emailEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                passwordEditText.requestFocus();
                return true;
            }
            return false;
        });
        passwordEditText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            SystemUtil.hideSoftKeyboard(getActivity());
            signIn(emailEditText.getText().toString(), passwordEditText.getText().toString());
            return true;
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

    private void requestPasswordFocus() {
        passwordEditText.requestFocus();
    }

    private void requestEmailFocus() {
        emailEditText.requestFocus();
    }

    private void setPasswordError(String error) {
        passwordTextInputLayout.setErrorEnabled(true);
        passwordTextInputLayout.setError(error);
    }

    private void setEmailError(String error) {
        signInEmailTextInputLayout.setErrorEnabled(true);
        signInEmailTextInputLayout.setError(error);
    }

    private void setSignInButtonClickable(boolean clickable) {
        signInButton.setClickable(clickable);
    }

    private void setSignInButtonEnabled(boolean enable) {
        signInButton.setEnabled(enable);
    }


    /**
     * Generates an asymmetric key pair in the Android Keystore. Every use of the private key must
     * be authorized by the user authenticating with fingerprint. Public key use is unrestricted.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void createKeyPair() {
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
        } catch (GeneralSecurityException | ProviderException e) {
            Log.e("Breeze", e.getLocalizedMessage());
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

    @Override
    public void onPause() {
        super.onPause();
    }
}