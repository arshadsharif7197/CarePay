package com.carecloud.carepaylibray.cognito;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.signinsignup.dtos.SignInLablesDTO;

/**
 * Activity used to confirm a signed-up user
 */
public class SignUpConfirmActivity extends BaseActivity {
    private EditText username;
    private EditText confCode;

    private Button      confirm;
    private TextView    reqCode;
    private String      userName;
    private AlertDialog userDialog;
    private SignInLablesDTO signInLablesDTO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_confirm);
        signInLablesDTO = (SignInLablesDTO) getIntent().getExtras().get("signInLablesDTO");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        TextView main_title = (TextView) findViewById(R.id.confirm_toolbar_title);
        main_title.setText(signInLablesDTO.getConfirm());

        init();
    }

    private void init() {

        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if(extras.containsKey("name")) {
                userName = extras.getString("name");
                username = (EditText) findViewById(R.id.editTextConfirmUserId);
                username.setText(userName);

                confCode = (EditText) findViewById(R.id.editTextConfirmCode);
                confCode.requestFocus();

                if(extras.containsKey("destination")) {
                    String dest = extras.getString("destination");
                    String delMed = extras.getString("deliveryMed");

                    TextView screenSubtext = (TextView) findViewById(R.id.textViewConfirmSubtext_1);
                    if(dest != null && delMed != null && dest.length() > 0 && delMed.length() > 0) {
                        screenSubtext.setText(String.format(signInLablesDTO.getConfirmCodeSentToWithArgument(),dest,delMed));
                    }
                    else {
                        screenSubtext.setText(signInLablesDTO.getConfirmConfirmationCodeSent());
                    }
                }
            }
            else {
                TextView screenSubtext = (TextView) findViewById(R.id.textViewConfirmSubtext_1);
                screenSubtext.setText(signInLablesDTO.getConfirmRequestForConfirmation());
            }

        }

        username = (EditText) findViewById(R.id.editTextConfirmUserId);
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdLabel);
                    label.setText(username.getHint());
                    username.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                                     R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdLabel);
                    label.setText("");
                }
            }
        });

        confCode = (EditText) findViewById(R.id.editTextConfirmCode);
        confCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText(confCode.getHint());
                    confCode.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                                     R.drawable.text_border_selector));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = (TextView) findViewById(R.id.textViewConfirmCodeLabel);
                    label.setText("");
                }
            }
        });

        confirm = (Button) findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfCode();
            }
        });

        reqCode = (TextView) findViewById(R.id.resend_confirm_req);
        reqCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqConfCode();
            }
        });
    }


    private void sendConfCode() {
        userName = username.getText().toString();
        String confirmCode = confCode.getText().toString();

        if(userName == null || userName.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(String.format(signInLablesDTO.getConfirmCannotEmptyWithArgument(),username.getHint()));
            username.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                             R.drawable.text_border_error));
            return;
        }

        if(confirmCode == null || confirmCode.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(String.format(signInLablesDTO.getConfirmCannotEmptyWithArgument(),confCode.getHint()));
            confCode.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                             R.drawable.text_border_error));
            return;
        }

        getAppAuthoriztionHelper().getPool().getUser(userName).confirmSignUpInBackground(confirmCode, true, confHandler);
    }

    private void reqConfCode() {
        userName = username.getText().toString();
        if(userName == null || userName.length() < 1) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(String.format(signInLablesDTO.getConfirmCannotEmptyWithArgument(),username.getHint()));
            username.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                             R.drawable.text_border_error));
            return;
        }
        getAppAuthoriztionHelper().getPool().getUser(userName).resendConfirmationCodeInBackground(resendConfCodeHandler);

    }

    GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            showDialogMessage(signInLablesDTO.getConfirmSuccess(),
                    String.format(signInLablesDTO.getConfirmConfirmedWithArgument(),userName), true);
        }

        @Override
        public void onFailure(Exception exception) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(signInLablesDTO.getConfirmConfirmationFailed());
            username.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                             R.drawable.text_border_error));

            label = (TextView) findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(signInLablesDTO.getConfirmConfirmationFailed());
            confCode.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                             R.drawable.text_border_error));

            showDialogMessage("Confirmation failed", getAppAuthoriztionHelper().formatException(exception), false);
        }
    };

    VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            TextView mainTitle = (TextView) findViewById(R.id.textViewConfirmTitle);
            mainTitle.setText(signInLablesDTO.getConfirmConfirmYourAccount());
            confCode = (EditText) findViewById(R.id.editTextConfirmCode);
            confCode.requestFocus();
            showDialogMessage(signInLablesDTO.getConfirmConfirmationCodeSent(),
                    String.format(signInLablesDTO.getConfirmCodeSentToWithArgument(),cognitoUserCodeDeliveryDetails.getDestination(),
                            cognitoUserCodeDeliveryDetails.getDeliveryMedium()), false);
        }

        @Override
        public void onFailure(Exception exception) {
            TextView label = (TextView) findViewById(R.id.textViewConfirmUserIdMessage);
            label.setText(signInLablesDTO.getConfirmConfirmationCodeResendFailed());
            username.setBackground(ContextCompat.getDrawable(SignUpConfirmActivity.this,
                                                             R.drawable.text_border_error));
            showDialogMessage(signInLablesDTO.getConfirmConfirmationCodeRequestHasFailed(), getAppAuthoriztionHelper().formatException(exception), false);
        }
    };

    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton(signInLablesDTO.getConfirmOk(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exitActivity) {
                        exit();
                    }
                } catch (Exception e) {
                    exit();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    private void exit() {
        Intent intent = new Intent();
        if(userName == null)
            userName = "";
        intent.putExtra("name",userName);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }
}