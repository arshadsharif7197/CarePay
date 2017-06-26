package com.carecloud.carepay.mini.interfaces;

import com.carecloud.carepay.mini.services.ServiceHelper;
import com.carecloud.carepay.mini.utils.ApplicationPreferences;

/**
 * Created by lmenendez on 6/23/17
 */

public interface ApplicationHelper {
    ServiceHelper getServiceHelper();

    ApplicationPreferences getApplicationPreferences();

}
