package com.carecloud.shamrocksdk.constants;

/**
 * Constants for handling connectivity.
 */

public class HttpConstants {

    public static final int CONNECT_TIMEOUT_MS = 15000;

    public static final int READ_TIMEOUT_MS = 60000;

    public static final int WRITE_TIMEOUT_MS = 60000;

    public static final String CONNECTION_ISSUE_ERROR_MESSAGE = "Device issue. There was a problem with your request. Please try again later.";

    private static String X_API_KEY;
    private static String apiBaseURL = "https://payments.development.carecloud.com/";
    private static String DEEPSTREAM_URL = "wss://deepstream.development.carecloud.com";

    /**
     * Get the base url for Shamrock API
     * @return base url
     */
    public static String getAPIBaseURL() {
        return apiBaseURL;
    }

    /**
     * Set the base url for Shamrock API
     * @param apiBaseURL base url
     */
    public static void setApiBaseURL(String apiBaseURL) {
        HttpConstants.apiBaseURL = apiBaseURL;
    }

    /**
     * Get the deepstream server url for application
     * @return deepstream url
     */
    public static String getDeepstreamUrl() {
        return DEEPSTREAM_URL;
    }

    /**
     * Specify the deepstream url
     * @param deepstreamUrl deepstream url
     */
    public static void setDeepstreamUrl(String deepstreamUrl) {
        DEEPSTREAM_URL = deepstreamUrl;
    }

    /**
     * Get the API key for accessing Shamrock API
     * @return api key
     */
    public static String getxApiKey() {
        return X_API_KEY;
    }

    /**
     * Set the API key for accessing Shamrock API
     * @param xApiKey api key
     */
    public static void setxApiKey(String xApiKey) {
        X_API_KEY = xApiKey;
    }
}