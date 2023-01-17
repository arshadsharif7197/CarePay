package com.carecloud.shamrocksdk.registrations.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model Class for registering Device with Shamrock Payments
 */


public class Device {
    @SerializedName("organization_id")
    @Expose
    private String organizationId;
    @SerializedName("name")
    @Expose
    private String deviceName;
    @SerializedName("serial")
    @Expose
    private String serialNumber;
    @SerializedName("welcomeMessage")
    @Expose
    private String welcomeMessage;
    @SerializedName("device_group")
    @Expose
    private DeviceGroup deviceGroup;

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(DeviceGroup deviceGroup) {
        this.deviceGroup = deviceGroup;
    }


}