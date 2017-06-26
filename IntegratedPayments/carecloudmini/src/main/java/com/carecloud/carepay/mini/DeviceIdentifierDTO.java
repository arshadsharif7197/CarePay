package com.carecloud.carepay.mini;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * This is the device identifier class contains device information.
 * toString method will return json string for this class
 */

public class DeviceIdentifierDTO {
    @SerializedName("deviceIdentifier")
    @Expose
    private String deviceIdentifier;
    @SerializedName("deviceType")
    @Expose
    private String deviceType;
    @SerializedName("deviceSystemVersion")
    @Expose
    private String deviceSystemVersion;

    /**
     *
     * @return
     * The deviceIdentifier
     */
    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    /**
     *
     * @param deviceIdentifier
     * The deviceIdentifier
     */
    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    /**
     *
     * @return
     * The deviceType
     */
    public String getDeviceType() {
        return deviceType;
    }

    /**
     *
     * @param deviceType
     * The deviceType
     */
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /**
     *
     * @return
     * The deviceSystemVersion
     */
    public String getDeviceSystemVersion() {
        return deviceSystemVersion;
    }

    /**
     *
     * @param deviceSystemVersion
     * The deviceSystemVersion
     */
    public void setDeviceSystemVersion(String deviceSystemVersion) {
        this.deviceSystemVersion = deviceSystemVersion;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return  gson.toJson(this);
    }
}
