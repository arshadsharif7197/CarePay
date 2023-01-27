package com.carecloud.shamrocksdk;

import com.carecloud.shamrocksdk.constants.HttpConstants;

/**
 * Initialize Shamrock SDK
 */

public class ShamrockSdk {

    /**
     * Initialize Shamrock SDK with x-api key corresponding to appropriate environment.
     * Default values will be used for urls
     * @param apiKey
     */
    @Deprecated
    public static void init(String apiKey){
        HttpConstants.setxApiKey(apiKey);
    }

    /**
     * Initialize Shamrock SDK
     * @param apiKey x-api key
     * @param deepstreamUrl deep stream url
     * @param baseUrl base shamrock payments url
     */
    public static void init(String apiKey, String deepstreamUrl, String baseUrl){
        HttpConstants.setxApiKey(apiKey);
        HttpConstants.setDeepstreamUrl(deepstreamUrl);
        HttpConstants.setApiBaseURL(baseUrl);
    }
}
