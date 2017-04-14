package com.carecloud.carepaylibray.utils;

import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

/**
 * Created by lmenendez on 4/13/17.
 */

public class MixPanelUtil {

    private static MixpanelAPI mixpanel = HttpConstants.getMixpanelAPI();

    public static void logEvent(String event){
        mixpanel.track(event);
    }

}
