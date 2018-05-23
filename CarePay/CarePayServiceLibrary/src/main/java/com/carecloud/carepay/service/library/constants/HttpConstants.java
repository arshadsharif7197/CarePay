package com.carecloud.carepay.service.library.constants;

import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

/**
 * Created by Jahirul Bhuiyan on 10/12/2016.
 */

public class HttpConstants {

    public static final int CONNECT_TIMEOUT_MS = 15000;

    public static final int READ_TIMEOUT_MS = 60000;

    public static final int WRITE_TIMEOUT_MS = 60000;

    private static String apiBaseUrl;

    private static String apiStartUrl;

    private static String apiStartKey;

    private static String messagingBaseUrl;

    private static String pushNotificationWebclientUrl;

    private static boolean useUnifiedAuth = false;

    private static MixpanelAPI mixpanelAPI;

    private static String environment;

    private static String deepStreamUrl;

    private static String paymentsUrl;

    private static String paymentsApiKey;

    private static String retailUrl;


    private static DeviceIdentifierDTO deviceInformation;
    private static String formsUrl;

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

    public static MixpanelAPI getMixpanelAPI() {
        if(getEnvironment().equals("Production")) {
            return mixpanelAPI;
        }
        return mixpanelAPI;//set this to null to disable logging in other environments
    }

    public static void setMixpanelAPI(MixpanelAPI mixpanelAPI) {
        HttpConstants.mixpanelAPI = mixpanelAPI;
    }

    public static String getMessagingBaseUrl() {
        return messagingBaseUrl;
    }

    public static void setMessagingBaseUrl(String messagingBaseUrl) {
        HttpConstants.messagingBaseUrl = messagingBaseUrl;
    }

    public static void setFormsUrl(String formsUrl) {
        HttpConstants.formsUrl = formsUrl;
    }

    public static String getFormsUrl() {
        return formsUrl;
    }

    public static String getEnvironment() {
        return environment;
    }

    public static void setEnvironment(String environment) {
        HttpConstants.environment = environment;
    }

    public static String getDeepStreamUrl() {
        return deepStreamUrl;
    }

    public static void setDeepStreamUrl(String deepStreamUrl) {
        HttpConstants.deepStreamUrl = deepStreamUrl;
    }

    public static String getPaymentsUrl() {
        return paymentsUrl;
    }

    public static void setPaymentsUrl(String paymentsUrl) {
        HttpConstants.paymentsUrl = paymentsUrl;
    }

    public static String getPaymentsApiKey() {
        return paymentsApiKey;
    }

    public static void setPaymentsApiKey(String paymentsApiKey) {
        HttpConstants.paymentsApiKey = paymentsApiKey;
    }

    public static String getRetailUrl() {
        return retailUrl;
    }

    public static void setRetailUrl(String retailUrl) {
        HttpConstants.retailUrl = retailUrl;
    }
}
