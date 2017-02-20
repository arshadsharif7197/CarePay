package com.carecloud.carepaylibray.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;

public abstract class BaseActivity extends AppCompatActivity implements ISession {

    private ApplicationPreferences applicationPreferences;
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
        return ((ISession) getApplication()).getApplicationPreferences();
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        return ((ISession) getApplication()).getWorkflowServiceHelper();
    }

    public Context getContext(){
        return this;
    }
}
