package com.carecloud.shamrocksdk.connections.models;

import com.carecloud.shamrocksdk.connections.models.defs.DeviceDef;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Model class for working with Devices in DeepStream
 */

public class Device {
    private static final String PAYMENTS_PATH_PREFIX = "payment_request/";
    private static final String REFUNDS_PATH_PREFIX = "refund_request/";
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);


    @SerializedName("id")
    private String deviceId;

    @SerializedName("organization_id")
    private String organizationId;

    @SerializedName("device_group_id")
    private String deviceGroupId;

    @SerializedName("serial")
    private String serial;

    @SerializedName("name")
    private String name;

    @SerializedName("auth_token")
    private String authToken;

    @SerializedName("active")
    private String activeStatus;

    @SerializedName("created_at")
    private String createdDate;

    @SerializedName("metadata")
    private JsonElement metadata;

    @SerializedName("state")
    @DeviceDef.ConnectionState
    private String state;

    @SerializedName("payment_request_id")
    private String paymentRequestId;

    @SerializedName("is_refunding")
    private boolean refunding = false;

    @SerializedName("last_updated")
    private Long lastUpdated;

    private transient boolean processing = false;

    /**
     * Get the DeepStream ID of device
     * @return device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Get the Organization ID that this device is registered to
     * @return organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Get the Name of the device. Device names should be unique within a device group.
     * @return device name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the device group id where this device is registered.
     * @return device group id
     */
    public String getDeviceGroupId() {
        return deviceGroupId;
    }

    /**
     * Get device authentication token
     * @return auth token
     */
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Get the created date for device. This is the date the device was registered with Shamrock Paymments
     * @return created date
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * Get the Device state. See {@link DeviceDef DeviceDef} for available states
     * @return device state
     */
    public @DeviceDef.ConnectionState String getState() {
        return state;
    }

    public void setState(@DeviceDef.ConnectionState String state) {
        this.state = state;
    }

    /**
     * Get the current payment request id. This will be null if the device is not currently processing any request
     * @return payment request id or null
     */
    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    /**
     * Returns true if the device is currently processing a payment request. This can be used in conjunction with the device state.
     * @return true when device is processing a request
     */
    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    /**
     * Returns true if device has received a refund request, false if it has received a payment request.
     * This method will also validate the request prefix in addition to checking the refund flag
     * @return true if request is a refund request
     */
    public boolean isRefunding() {
        return refunding &&
                paymentRequestId != null &&
                paymentRequestId.contains(REFUNDS_PATH_PREFIX);
    }

    public void setRefunding(boolean refunding) {
        this.refunding = refunding;
    }


    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated() {
        long epoch = System.currentTimeMillis()/1000;
        this.lastUpdated = epoch;
    }


    public JsonElement getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonElement metadata) {
        this.metadata = metadata;
    }
}
