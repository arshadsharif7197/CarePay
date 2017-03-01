package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.utils.CustomPopupNotification;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;

public abstract class BaseActivity extends AppCompatActivity implements ISession {

    private Dialog progressDialog;
    private CustomPopupNotification errorNotification;
    private boolean isVisible = false;

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        return ((IApplicationSession) getApplication()).getApplicationPreferences();
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((IApplicationSession) getApplication()).getWorkflowServiceHelper();
    }

    @Override
    public CognitoAppHelper getCognitoAppHelper() {
        return ((IApplicationSession) getApplication()).getCognitoAppHelper();
    }

    @Override
    public ApplicationMode getApplicationMode() {
        return ((IApplicationSession) getApplication()).getApplicationMode();
    }

    public Context getContext(){
        return this;
    }

    @Override
    public void showProgressDialog() {
        if (!isVisible()) {
            return;
        }

        if(null != progressDialog && progressDialog.isShowing()) {
            return;
        }

        if(null == progressDialog) {
            progressDialog = new ProgressDialogUtil(this);
        }

        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if(null != progressDialog && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void showErrorNotification(String errorMessage) {
        if (!isVisible()) {
            return;
        }

        if(null != errorNotification && errorNotification.isShowing()) {
            return;
        }
        try{
            if(null == errorNotification) {

                errorNotification = new CustomPopupNotification(getContext(), getCurrentFocus(), errorMessage, CustomPopupNotification.TYPE_ERROR_NOTIFICATION, getCancelReasonAppointmentDialogListener());
            }
            errorNotification.showPopWindow();
        } catch (Exception e) {
            Log.e("Base Activity", e.getMessage());
        }
    }

    /**
     * Gets cancel reason appointment dialog listener.
     *
     * @return the cancel reason appointment dialog listener
     */
    public CustomPopupNotification.CustomPopupNotificationListener getCancelReasonAppointmentDialogListener() {
        return new CustomPopupNotification.CustomPopupNotificationListener() {
            @Override
            public void onSwipe(String swipeDirection) {
                hideErrorNotification();
            }
        };
    }

    @Override
    public void hideErrorNotification() {
        if(null != errorNotification && errorNotification.isShowing()){
            errorNotification.dismiss();
            errorNotification = null;
        }
    }
}
