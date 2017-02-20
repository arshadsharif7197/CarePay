package com.carecloud.carepaylibray.base;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;

/**
 * Created by cocampo on 2/20/17.
 */

public interface IApplicationSession {
    ApplicationPreferences getApplicationPreferences();

    WorkflowServiceHelper getWorkflowServiceHelper();
}
