package com.carecloud.carepay.patient.demographics.fragments.settings;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.customcomponents.CarePayButton;
import com.carecloud.carepaylibray.customcomponents.CarePayEditText;
import com.carecloud.carepaylibray.customcomponents.CarePayTextView;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ChangeEmailDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    private UpdateEmailFragment.LargeAlertInterface largeAlertInterface;

    String email;
    String type = "";
    CarePayTextView dialogMessageTextView;
    CarePayTextView dialogTitleTextView;
    CarePayTextView emailResendTextView;
    public CarePayEditText editTextVerificationCodeEmail;
    CarePayButton verifyButton;
    CountDownTimer timer;

    public static ChangeEmailDialogFragment newInstance(String type, String email) {
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("type", type);
        ChangeEmailDialogFragment fragment = new ChangeEmailDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.verifyButton) {
            if (SystemUtil.isNotEmptyString(editTextVerificationCodeEmail.getText().toString())) {
                largeAlertInterface.onActionButton(editTextVerificationCodeEmail.getText().toString());

            }
        } else if (viewId == R.id.closeViewLayout) {
            timer.cancel();
            cancel();

        } else if (viewId == R.id.emailResendTextView) {
            largeAlertInterface.onActionButton("resend");
        }
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        email = args.getString("email");
        type = args.getString("type");


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_change_email_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verifyButton = (CarePayButton) findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(this);
        verifyButton.setEnabled(false);
        findViewById(R.id.emailResendTextView).setOnClickListener(this);
        findViewById(R.id.closeViewLayout).setOnClickListener(this);
        dialogMessageTextView = (CarePayTextView) findViewById(R.id.dialogMessageTextView);
        dialogTitleTextView = (CarePayTextView) findViewById(R.id.dialogTitleTextView);
        editTextVerificationCodeEmail = (CarePayEditText) findViewById(R.id.editTextVerificationCodeEmail);
        emailResendTextView = (CarePayTextView) findViewById(R.id.emailResendTextView);
        dialogTitleTextView.setText("Two-Factor Authentication");
        if (type.equals("login")) {
            countDownMethod();
            // emailResendTextView.setVisibility(View.GONE);
            dialogMessageTextView.setText("Two-Factor authentication is enabled ,We have sent you 5 digit code to" + " " + email + ". " + "Please verify that its you trying to access your account");
        } else if (type.equals("emailEnable") || type.equals("smsVerification")) {
            //emailResendTextView.setVisibility(View.GONE);
            countDownMethod();
            dialogMessageTextView.setText("A 5 digit security code has been sent to your" + " " + email + ". " + "Enter verification code");
        } else {
            dialogMessageTextView.setText("A 5 digit security code has been sent to your" + " " + email + ". " + "Please verify that its you trying to update your account");
            countDownMethod();
        }
        setTextChangeListener();
    }

    private void setTextChangeListener() {
        editTextVerificationCodeEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                enableVerifyReady();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }


        });
    }

    private void enableVerifyReady() {
        boolean isReady = editTextVerificationCodeEmail.getText().toString().length() == 5;
        verifyButton.setEnabled(isReady);
    }

    private void countDownMethod() {
        timer = new CountDownTimer(300 * 1000, 100 * 10) {

            public void onTick(long millisUntilFinished) {
                // Used for formatting digit to be in 2 digits only
                NumberFormat f = new DecimalFormat("00");
                // long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                setTextUnderlined(emailResendTextView);
                emailResendTextView.setText("Resend in " + f.format(min) + " : " + f.format(sec));
            }

            // When the task is over it will print 00:00:00 there

            public void onFinish() {

                emailResendTextView.setText("00:00");
                largeAlertInterface.onActionButton("resend");
                timer.start();

            }

        }.start();
    }


    public void setLargeAlertInterface(UpdateEmailFragment.LargeAlertInterface
                                               largeAlertInterface) {
        this.largeAlertInterface = largeAlertInterface;
    }

    private void setTextUnderlined(CarePayTextView textView) {
        SpannableString content = new SpannableString("Resend code");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

    }
}