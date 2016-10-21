package com.carecloud.carepay.practice.library.signin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.homescreen.CloverMainActivity;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.demographics.adapters.CustomAlertAdapter;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.Arrays;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;

/**
 * Created by Jahirul Bhuiyan on 10/13/2016.
 * This is the Launcher activity for the practice app
 * Cogtino SDK use for user authentication
 * On success authentication screen will navigate next screen from the transition Json
 * On failed showing the authentication failure dialog with no navigation
 */
public class SigninActivity extends AppCompatActivity {

    private TextView signinButton;
    private TextView forgotPasswordButton;
    private TextView languageButton;

    private TextInputLayout signInEmailTextInputLayout;
    private TextInputLayout passwordTextInputLayout;

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar progressBar;

    private boolean isEmptyEmail;
    private boolean isEmptyPassword;
    private ImageView rightarrow;

    private String[] language = {"EN", "SP"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        CognitoAppHelper.init(this);

        setContentView(R.layout.activity_signin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setSystemUiVisibility();
        progressBar = (ProgressBar) findViewById(R.id.signInProgress);
        progressBar.setVisibility(View.INVISIBLE);

        initViews();
        setClicables();
        setEditTexts();
    }


    public void initViews() {
        signinButton = (TextView) findViewById(R.id.signinTextview);
        signinButton.setEnabled(false);
        if (!signinButton.isEnabled()) {
            rightarrow = (ImageView) findViewById(R.id.rightarrow);
            rightarrow.setAlpha(50);
        }
        signinButton.setTextColor(signinButton.getTextColors().withAlpha(50));
        forgotPasswordButton = (TextView) findViewById(R.id.forgot_passwordTextview);
        languageButton = (TextView) findViewById(R.id.languageTextview);
        signInEmailTextInputLayout = (TextInputLayout) findViewById(R.id.signInEmailTextInputLayout);
        passwordTextInputLayout = (TextInputLayout) findViewById(R.id.passwordTextInputLayout);
    }


    private void setClicables() {

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (areAllValid()) {
                    signInUser();
                }
            }
        });


        languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(SigninActivity.this);
                dialog.setTitle("Select Language");
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                View customView = LayoutInflater.from(SigninActivity.this).inflate(R.layout.alert_list_practice_layout, null, false);
                ListView listView = (ListView) customView.findViewById(R.id.dialoglist_practice);
                CustomAlertAdapter mAdapter = new CustomAlertAdapter(SigninActivity.this, Arrays.asList(language));
                listView.setAdapter(mAdapter);
                dialog.setView(customView);

                final AlertDialog alert = dialog.create();
                alert.show();
                alert.getWindow().setLayout(500,350);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        languageButton.setText(language[position]);
                        alert.dismiss();
                    }
                });

            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(SigninActivity.this);
                View customview=LayoutInflater.from(SigninActivity.this).inflate(R.layout.patient_mode_switch_dialog, null, false);
                dialog.setView(customview);
                final AlertDialog alert = dialog.create();
                alert.show();
                alert.getWindow().setLayout(500,450);
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


    private void setEditTexts() {
        signInEmailTextInputLayout.setTag("Not Defined");
        emailEditText = (EditText) findViewById(R.id.signinEmailpracticeEditText);
        emailEditText.setHint("Not Defined");
        emailEditText.setTag(signInEmailTextInputLayout);

        passwordTextInputLayout.setTag("Not Defined");
        passwordEditText = (EditText) findViewById(R.id.passwordpracticeEditText);
        passwordEditText.setTag(passwordTextInputLayout);

        setTextListeners();
        setChangeFocusListeners();

        emailEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    private void setChangeFocusListeners() {
        emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(SigninActivity.this);
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    SystemUtil.showSoftKeyboard(SigninActivity.this);
                }
                SystemUtil.handleHintChange(view, b);
            }
        });
    }

    private void enableSigninButton() {
        boolean areAllNonEmpty = !(isEmptyEmail || isEmptyPassword);
        signinButton.setEnabled(areAllNonEmpty);
    }

    private void signInUser() {
        Log.v(LOG_TAG, "sign in user");
        String userName = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        CognitoAppHelper.signIn(userName, password, cognitoActionCallback);

    }

    CognitoActionCallback cognitoActionCallback = new CognitoActionCallback() {
        @Override
        public void onLoginSuccess() {
            progressBar.setVisibility(View.INVISIBLE);
            launchHomescreen();
        }

        @Override
        public void onBeforeLogin() {
            SystemUtil.hideSoftKeyboard(SigninActivity.this);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onLoginFailure(String exceptionMessage) {
            SystemUtil.showDialogMessage(SigninActivity.this,
                    "Sign-in failed",
                    "Invalid user id or password");

        }
    };

    private void launchHomescreen() {
        Intent intent = new Intent(this, CloverMainActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void setSystemUiVisibility() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
