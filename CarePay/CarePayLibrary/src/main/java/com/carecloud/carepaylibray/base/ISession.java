package com.carecloud.carepaylibray.base;

import com.carecloud.carepay.service.library.ApplicationPreferences;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;

/**
 * Created by cocampo on 2/17/17.
 */

public interface ISession extends IApplicationSession {
    void showProgressDialog();

    void hideProgressDialog();
}
