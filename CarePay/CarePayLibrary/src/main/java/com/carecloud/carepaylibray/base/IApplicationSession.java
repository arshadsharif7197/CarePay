package com.carecloud.carepaylibray.base;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.cognito.AppAuthoriztionHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;

/**
 * Created by cocampo on 2/20/17.
 */

public interface IApplicationSession {
    ApplicationPreferences getApplicationPreferences();

    WorkflowServiceHelper getWorkflowServiceHelper();

    AppAuthoriztionHelper getAppAuthoriztionHelper();

    ApplicationMode getApplicationMode();
}
