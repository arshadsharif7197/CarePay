package com.carecloud.carepay.practice.library.payments.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/9/17
 */

public class IntegratedPaymentDeviceGroup {

    @SerializedName("id")
    private String groupId;

    @SerializedName("name")
    private String groupName;

    @SerializedName("organization_id")
    private String organizationId;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
}
