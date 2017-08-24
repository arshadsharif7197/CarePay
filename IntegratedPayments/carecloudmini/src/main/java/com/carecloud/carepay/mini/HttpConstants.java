package com.carecloud.carepay.mini;

/**
 * Created by Jahirul Bhuiyan on 10/12/2016
 */

public class HttpConstants {

    public static final int CONNECT_TIMEOUT_MS = 15000;

    public static final int READ_TIMEOUT_MS = 60000;

    public static final int WRITE_TIMEOUT_MS = 60000;

    private static String apiBaseUrl;

    private static String apiStartUrl;

    private static  String apiStartKey;

    private static  String pushNotificationWebclientUrl;

    private static boolean useUnifiedAuth = false;

    private static DeviceIdentifierDTO deviceInformation;

    public static DeviceIdentifierDTO getDeviceInformation() {
        return deviceInformation;
    }

    public static void setDeviceInformation(DeviceIdentifierDTO deviceInformation) {
        HttpConstants.deviceInformation = deviceInformation;
    }

    public static String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public static void setApiBaseUrl(String apiBaseUrl) {
        HttpConstants.apiBaseUrl = apiBaseUrl;
    }

    public static String getApiStartKey() {
        return apiStartKey;
    }

    public static void setApiStartKey(String apiStartKey) {
        HttpConstants.apiStartKey = apiStartKey;
    }

    public static String getPushNotificationWebclientUrl() {
        return pushNotificationWebclientUrl;
    }

    public static void setPushNotificationWebclientUrl(String pushNotificationWebclientUrl) {
        HttpConstants.pushNotificationWebclientUrl = pushNotificationWebclientUrl;
    }


    public static boolean isUseUnifiedAuth() {
        return useUnifiedAuth;
    }

    public static void setUseUnifiedAuth(boolean useUnifiedAuth) {
        HttpConstants.useUnifiedAuth = useUnifiedAuth;
    }
}
