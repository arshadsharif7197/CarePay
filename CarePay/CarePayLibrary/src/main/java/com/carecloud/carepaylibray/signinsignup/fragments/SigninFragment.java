package com.carecloud.carepaylibray.signinsignup.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.activities.LibraryMainActivity;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.cognito.AppHelper;
import com.carecloud.carepaylibray.signinsignup.SigninSignupActivity;
import com.carecloud.carepaylibray.signinsignup.models.TextWatcherModel;
import com.carecloud.carepaylibray.utils.StringUtil;

import java.util.Locale;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by harish_revuri on 9/7/2016.
 */
public class SigninFragment extends Fragment {

    private TextInputLayout emailHint, passwordHint;
    private EditText emailEditText;
    private EditText passwordEditText;
    private TextView changeLanguageTextView, forgotPasswordTextView;
    private Button signinButton;
    private Button signupButton;
    private boolean isValidEmail, isValidPassword;
    private Typeface hintFontFamily;
    private Typeface editTextFontFamily;
    private Typeface floatingTextFontfamily;
    private Typeface buttonFontFamily;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        emailHint = (TextInputLayout) view.findViewById(R.id.signInEmailTextInputLayout);
        passwordHint = (TextInputLayout) view.findViewById(R.id.passwordTextInputLayout);

        emailEditText = (EditText) view.findViewById(R.id.signinEmailEditText);
        passwordEditText = (EditText) view.findViewById(R.id.passwordEditText);

        signinButton = (Button) view.findViewById(R.id.signin_button);
        signupButton = (Button) view.findViewById(R.id.signup_button);

        // TODO: 9/14/2016 replace with SystemUtil.setTypeFace...
        hintFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_regular.otf");
        editTextFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");
        floatingTextFontfamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/proximanova_semibold.otf");
        buttonFontFamily = Typeface.createFromAsset(getResources().getAssets(), "fonts/gotham_rounded_medium.otf");

        emailEditText.setTypeface(editTextFontFamily);
        passwordEditText.setTypeface(editTextFontFamily);
        emailHint.setTypeface(hintFontFamily);
        passwordHint.setTypeface(hintFontFamily);
        signinButton.setTypeface(buttonFontFamily);
        signupButton.setTypeface(buttonFontFamily);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInfo()) {
//                    Intent intent = new Intent(getContext(), AppointmentsActivity.class);
//                    startActivity(intent);
                    signInUser();
                }
            }
        });

        signupButton = (Button) view.findViewById(R.id.signup_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                SignupFragment fragment = (SignupFragment) fm.findFragmentByTag(SignupFragment.class.getSimpleName());
                if (fragment == null) {
                    fragment = new SignupFragment();
                }
                fm.beginTransaction()
                        .replace(R.id.signin_layout, fragment, SignupFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });

        changeLanguageTextView = (TextView) view.findViewById(R.id.changeLanguageText);
        forgotPasswordTextView = (TextView) view.findViewById(R.id.forgotPasswordTextView);
        changeLanguageTextView.setTypeface(editTextFontFamily);
        forgotPasswordTextView.setTypeface(editTextFontFamily);
        changeLanguageTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), LibraryMainActivity.class);
                startActivity(intent);
            }
        });

        setTextListeners();


        return view;
    }

    /**
     * Set the focus change and selct text listeners for the edit texts
     */
    private void setTextListeners() {
        emailEditText.addTextChangedListener(
                new TextWatcherModel(editTextFontFamily,
                                     floatingTextFontfamily,
                                     TextWatcherModel.InputType.TYPE_EMAIL,
                                     emailEditText,
                                     emailHint,
                                     "Enter Valid Email",
                                     false,
                                     new TextWatcherModel.OnInputChangedListner() {
                                         @Override
                                         public void OnInputChangedListner(boolean isValid) {
                                             isValidEmail = isValid;
                                             checkForButtonEnable();
                                         }
                                     }));
        passwordEditText.addTextChangedListener(new TextWatcherModel(editTextFontFamily,
                                                                     floatingTextFontfamily,
                                                                     TextWatcherModel.InputType.TYPE_PASSWORD,
                                                                     passwordEditText, passwordHint,
                                                                     "Enter password", false, new TextWatcherModel.OnInputChangedListner() {
            @Override
            public void OnInputChangedListner(boolean isValid) {
                isValidPassword = isValid;
                checkForButtonEnable();
            }
        }));

        // change hint to caps when floating hint
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                String hint = getString(R.string.email_text);
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    emailHint.setHint(hintCaps);
                } else {
                    if(StringUtil.isNullOrEmpty(emailEditText.getText().toString())) {
                        // change hint to lower
                        emailHint.setHint(hint);

                    } else {
                        emailEditText.setHint(hint);
                    }
                }
            }
        });
        emailEditText.clearFocus();

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.v(LOG_TAG, "password has focus");
                String hint = getString(R.string.password_text);
                String hintCaps = hint.toUpperCase();
                if (hasFocus) {
                    // change hint to all caps
                    passwordHint.setHint(hintCaps);
                } else {
                    if(StringUtil.isNullOrEmpty(passwordEditText.getText().toString())) {
                        passwordHint.setHint(hint);
                    } else {
                        // change hint to lower
                        passwordEditText.setHint(hintCaps);
                    }
                }
            }
        });
        passwordEditText.clearFocus();
    }

    private boolean isValidInfo() {

        if (isValidEmail && isValidPassword) {
            return true;
        } else if (!isValidEmail && !isValidPassword) {
            Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_LONG).show();
            return false;
        } else if (!isValidEmail) {
            Toast.makeText(getActivity(), "Enter valid Email-ID", Toast.LENGTH_LONG).show();
            return false;
        } else if (!isValidPassword) {
            Toast.makeText(getActivity(), "Enter valid password", Toast.LENGTH_LONG).show();
            return false;
        } else {
            Toast.makeText(getActivity(), "Fields can't be empty", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void checkForButtonEnable() {
        if (isValidEmail && isValidPassword) {
            signinButton.setBackgroundResource(R.drawable.button_selector);
            signinButton.setTextColor(Color.WHITE);
        } else {
            signinButton.setBackgroundResource(R.drawable.button_light_gray_background);

        }
    }

    // cognito
    private AlertDialog    userDialog;
    private ProgressDialog waitDialog;

    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(getActivity());
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    private void exit(String uname) {
        exit(getActivity(), uname, null);
    }

    private void exit(Activity activity, String uname, String password) {
//        Intent intent = new Intent();
//        if (uname == null) {
//            uname = "";
//        }
//        if (password == null) {
//            password = "";
//        }
//        intent.putExtra("name", uname);
//        intent.putExtra("password", password);
//        activity.setResult(RESULT_OK, intent);

        Log.v(LOG_TAG, "Login complete!");

        // for demo, go back to the login screen
//        FragmentManager fm = getFragmentManager();
//        SigninFragment signinFragment = (SigninFragment) fm.findFragmentByTag(SigninFragment.class.getSimpleName());
//        if(signinFragment == null) {
//            signinFragment = new SigninFragment();
//        }
//        getFragmentManager()
//                .beginTransaction()
//                .replace(R.id.layoutSigninSignup, signinFragment, SigninFragment.class.getSimpleName())
//                .commit();
//
        // go to Appointments
        Intent intent = new Intent(activity, AppointmentsActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            Log.e(LOG_TAG, "Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            closeWaitDialog();
            launchUser();
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            closeWaitDialog();
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            closeWaitDialog();
//            mfaAuth(multiFactorAuthenticationContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            showDialogMessage("Sign-in failed", AppHelper.formatException(e));
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            // TODO change the place holder
        }
    };

    private void signInUser() {
        Log.v(LOG_TAG, "signInUser()");
        String username = emailEditText.getText().toString();

        AppHelper.setUser(username);

        String password = passwordEditText.getText().toString();

        showWaitDialog("Signing in...");
        AppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
    }

    private void launchUser() {
        Intent userActivity = new Intent(getActivity(), AppointmentsActivity.class);
        startActivity(userActivity);
    }

    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {

        if(username != null) {
            AppHelper.setUser(username);
        }

        String password = passwordEditText.getText().toString();

        AuthenticationDetails authenticationDetails = new AuthenticationDetails(username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

}