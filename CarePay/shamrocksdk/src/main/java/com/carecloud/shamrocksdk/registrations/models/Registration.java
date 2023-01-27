package com.carecloud.shamrocksdk.registrations.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model Class for handling Device Registration response
 */

public class Registration {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("organization_id")
    @Expose
    private String organizationId;
    @SerializedName("serial")
    @Expose
    private String serial;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("active")
    @Expose
    private Boolean active;
    @SerializedName("metadata")
    @Expose
    private JsonObject metadata;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("device_group_id")
    @Expose
    private String deviceGroupId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public JsonObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeviceGroupId() {
        return deviceGroupId;
    }

    public void setDeviceGroupId(String deviceGroupId) {
        this.deviceGroupId = deviceGroupId;
    }
}