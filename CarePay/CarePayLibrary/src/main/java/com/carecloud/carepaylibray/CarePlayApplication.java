package com.carecloud.carepaylibray;

import android.support.multidex.MultiDexApplication;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.label.LabelProvider;
import com.carecloud.carepay.service.library.label.SharedPreferenceLabelProvider;
import com.carecloud.carepaylibray.base.IApplicationSession;

/**
 * Created by pjohnson on 15/03/17.
 */

public abstract class CarePlayApplication extends MultiDexApplication implements IApplicationSession {

    private ApplicationPreferences applicationPreferences;
    private WorkflowServiceHelper workflowServiceHelper;
    private AppAuthorizationHelper appAuthorizationHelper;
    private LabelProvider labelProvider;

    @Override
    public ApplicationPreferences getApplicationPreferences() {
        if (applicationPreferences == null) {
            applicationPreferences = new ApplicationPreferences(this);
        }

        return applicationPreferences;
    }

    public LabelProvider getLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new SharedPreferenceLabelProvider(this);
        }

        return labelProvider;
    }

    @Override
    public WorkflowServiceHelper getWorkflowServiceHelper() {
        if (workflowServiceHelper == null) {
            workflowServiceHelper = new WorkflowServiceHelper(getApplicationPreferences(), getLabelProvider(), getApplicationMode());
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
}
