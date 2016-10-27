package com.carecloud.carepay.service.library.constants;

import com.carecloud.carepay.service.library.dtos.DeviceIdentifierDTO;

/**
 * Created by Jahirul Bhuiyan on 10/12/2016.
 */

public class HttpConstants {

    public static final int CONNECT_TIMEOUT_MS = 15000;

    public static final int READ_TIMEOUT_MS = 15000;

    public static final int WRITE_TIMEOUT_MS = 15000;

    public static final String API_BASE_URL = "https://g8r79tifa4.execute-api.us-east-1.amazonaws.com";

    public static final String API_START_URL = "dev/workflow/carepay/practice_mode/practice_mode_signin/start";

    public static final String X_API_KEY = "VXlNfYaZYk4sxti0C4caw1v0J0dEDHXH9oQ3bPsA";


    private static DeviceIdentifierDTO deviceInformation;

    public static DeviceIdentifierDTO getDeviceInformation() {
        return deviceInformation;
    }

    public static void setDeviceInformation(DeviceIdentifierDTO deviceInformation) {
        HttpConstants.deviceInformation = deviceInformation;
    }
}
