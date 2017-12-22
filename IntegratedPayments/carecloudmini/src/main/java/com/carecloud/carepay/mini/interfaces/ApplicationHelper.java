package com.carecloud.carepay.mini.interfaces;

import com.carecloud.carepay.mini.models.response.Authentication;
import com.carecloud.carepay.mini.services.carepay.RestCallServiceHelper;
import com.carecloud.carepay.mini.utils.ApplicationPreferences;

/**
 * Created by lmenendez on 6/23/17
 */

public interface ApplicationHelper {

    RestCallServiceHelper getRestHelper();

    ApplicationPreferences getApplicationPreferences();

    void setAuthentication(Authentication authentication);

    Authentication getAuthentication();

}