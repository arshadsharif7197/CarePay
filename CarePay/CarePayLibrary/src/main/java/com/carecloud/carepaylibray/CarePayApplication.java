package com.carecloud.carepaylibray;

import android.support.multidex.MultiDexApplication;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepay.service.library.base.IApplicationSession;

/**
 * Created by pjohnson on 15/03/17
 */

public abstract class CarePayApplication extends MultiDexApplication implements IApplicationSession {

    private ApplicationPreferences applicationPreferences;
    private WorkflowServiceHelper workflowServiceHelper;
    private AppAuthorizationHelper appAuthorizationHelper;
    private long lastInteraction;

    @Override
    public void onCreate() {
        super.onCreate();
        Platform.setPlatform(new AndroidPlatform(this));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        if (applicationPreferences == null) {
            applicationPreferences = ApplicationPreferences.getInstance();
        }

        return applicationPreferences;
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        if (workflowServiceHelper == null) {
            workflowServiceHelper = new WorkflowServiceHelper(getApplicationPreferences(), getApplicationMode());
        }

        return workflowServiceHelper;
    }

    @Override
    public AppAuthorizationHelper getAppAuthorizationHelper() {
        if (appAuthorizationHelper == null) {
            appAuthorizationHelper = new AppAuthorizationHelper(this, getApplicationMode());
            getWorkflowServiceHelper().setAppAuthorizationHelper(appAuthorizationHelper);
        }

        return appAuthorizationHelper;
    }

    @Override
    public void onAtomicRestart(){
        workflowServiceHelper = null;
        appAuthorizationHelper = null;
        applicationPreferences = null;
    }


    @Override
    public void setLastInteraction(long systemTime){
        this.lastInteraction = systemTime;
    }

    @Override
    public long getLastInteraction(){
        return this.lastInteraction;
    }

}
