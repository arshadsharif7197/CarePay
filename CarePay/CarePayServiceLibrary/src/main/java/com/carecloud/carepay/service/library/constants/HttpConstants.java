package com.carecloud.carepay.service.library.constants;

import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;

/**
 * Created by Jahirul Bhuiyan on 10/12/2016.
 */

public class HttpConstants {

    public static final int CONNECT_TIMEOUT_MS = 15000;

    public static final int READ_TIMEOUT_MS = 15000;

    public static final int WRITE_TIMEOUT_MS = 15000;

    private static String apiBaseUrl;

    private static String apiStartUrl;

    private static  String xApiKey;

    private static  String pushNotificationWebclientUrl;


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

    public static String getApiStartUrl() {
        return apiStartUrl;
    }

    public static void setApiStartUrl(String apiStartUrl) {
        HttpConstants.apiStartUrl = apiStartUrl;
    }

    public static String getxApiKey() {
        return xApiKey;
    }

    public static void setxApiKey(String xApiKey) {
        HttpConstants.xApiKey = xApiKey;
    }

    public static String getPushNotificationWebclientUrl() {
        return pushNotificationWebclientUrl;
    }

    public static void setPushNotificationWebclientUrl(String pushNotificationWebclientUrl) {
        HttpConstants.pushNotificationWebclientUrl = pushNotificationWebclientUrl;
    }
}
