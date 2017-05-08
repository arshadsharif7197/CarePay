package com.carecloud.carepay.practice.library.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.customdialog.IConfirmPracticeAppPin;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.base.BaseActivity;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;

import org.apache.commons.lang3.NotImplementedException;

public abstract class BasePracticeActivity extends BaseActivity
        implements IConfirmPracticeAppPin {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSystemUiVisibility();
        setNavigationBarVisibility();
    }


    @Override
    public void onPinConfirmationCheck(boolean isCorrectPin, String pin) {

    }


    protected boolean setTextViewById(int id, String text) {
        View view = findViewById(id);
        if (null == view || !(view instanceof TextView)) {
            return false;
        }

        ((TextView) view).setText(text);

        return true;
    }

    public boolean enableViewById(int id) {
        return setEnabledViewById(id, true);
    }

    public boolean disableViewById(int id) {
        return setEnabledViewById(id, true);
    }

    private boolean setEnabledViewById(int id, boolean enabled) {
        View view = findViewById(id);
        if (null == view) {
            return false;
        }

        view.setEnabled(enabled);

        return true;
    }

    public boolean showViewById(int id) {
        return setVisibilityById(id, View.VISIBLE);
    }

    public boolean disappearViewById(int id) {
        return setVisibilityById(id, View.GONE);
    }

    public boolean hideViewById(int id) {
        return setVisibilityById(id, View.INVISIBLE);
    }

    private boolean setVisibilityById(int id, int visibility) {
        View view = findViewById(id);
        if (null == view) {
            return false;
        }

        view.setVisibility(visibility);

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CarePayConstants.CLOVER_PAYMENT_INTENT_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    processExternalPayment(PaymentExecution.clover, data);
                } else {
                    processExternalPaymentFailure(PaymentExecution.clover, resultCode);
                }

                break;
            }
            default: {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected void processExternalPayment(PaymentExecution paymentExecution, Intent data) {
        throw new NotImplementedException("Process external payment has not been implemented by " + getClass().getSimpleName());
    }

    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {

    }

    protected WorkflowServiceCallback homeCall = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            findViewById(R.id.btnHome).setEnabled(true);
            finish();
            PracticeNavigationHelper.navigateToWorkflow(getContext(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            findViewById(R.id.btnHome).setEnabled(false);
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    protected void goToHome(TransitionDTO logOutDto) {
        getWorkflowServiceHelper().execute(logOutDto, homeCall);
    }

    @Override
    public void navigateToWorkflow(WorkflowDTO workflowDTO) {
        PracticeNavigationHelper.navigateToWorkflow(this, workflowDTO);
    }
}
