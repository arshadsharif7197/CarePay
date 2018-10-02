package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

public class LatestVersionModel {

    @SerializedName("device_type")
    private String deviceType;

    @SerializedName("application_name")
    private String applicationName;

    @SerializedName("latest_version_name")
    private String versionName;

    @SerializedName("latest_version_number")
    private int versionNumber;

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }
}
