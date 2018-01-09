package com.carecloud.carepay.service.library.base;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;

/**
 * Created by cocampo on 2/20/17.
 */

public interface IApplicationSession {
    ApplicationPreferences getApplicationPreferences();

    WorkflowServiceHelper getWorkflowServiceHelper();

    AppAuthorizationHelper getAppAuthorizationHelper();

    ApplicationMode getApplicationMode();

    void onAtomicRestart();

    void setLastInteraction(long systemTime);

    long getLastInteraction();
}
