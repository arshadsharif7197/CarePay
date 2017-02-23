package com.carecloud.carepaylibray.base;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;

public abstract class BaseActivity extends AppCompatActivity implements ISession {

    private Dialog progressDialog;
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
}
