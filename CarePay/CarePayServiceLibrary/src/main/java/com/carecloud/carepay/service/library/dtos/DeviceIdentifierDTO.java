package com.carecloud.carepay.service.library.dtos;

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
    @SerializedName("deviceOSVersion")
    @Expose
    private String deviceOSVersion;
    private String version;
    private String deviceOS;

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
     * The deviceOSVersion
     */
    public String getDeviceOSVersion() {
        return deviceOSVersion;
    }

    /**
     *
     * @param deviceOSVersion
     * The deviceOSVersion
     */
    public void setDeviceOSVersion(String deviceOSVersion) {
        this.deviceOSVersion = deviceOSVersion;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return  gson.toJson(this);
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getDeviceOS() {
        return deviceOS;
    }
}
