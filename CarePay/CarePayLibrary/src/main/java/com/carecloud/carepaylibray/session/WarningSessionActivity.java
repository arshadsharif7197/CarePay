package com.carecloud.carepaylibray.session;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.base.BaseActivity;

/**
 * @author pjohnson on 2019-07-01.
 */
public abstract class WarningSessionActivity extends BaseActivity {

    private static final long ONE_SECOND = 1000;
    public int countDown = 60;
    private TextView counterTextView;
    private String alertMessage;
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning_session);
        setFinishOnTouchOutside(false);
        TextView logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            restartApp();
        });
        TextView continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(v -> {
            onContinueButton();
        });
        counterTextView = findViewById(R.id.counterTextView);
        alertMessage = Label.getLabel("practice.idleSignOut.message");
        counterTextView.setText(String.format(alertMessage, countDown--));
        runnable = this::countDownMethod;
        counterTextView.postDelayed(runnable, ONE_SECOND);
    }

    protected abstract void onContinueButton();

    private void countDownMethod() {
        if (countDown > 0) {
            counterTextView.setText(String.format(alertMessage, countDown--));
            runnable = this::countDownMethod;
            counterTextView.postDelayed(runnable, ONE_SECOND);
        } else {
            runnable = null;
            restartApp();
        }
    }

    protected abstract void restartApp();

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        //NA
    }

    @Override
    public void onBackPressed() {
        //disabled
    }

    @Override
    protected void stopSessionService() {
        //NA
    }
}
