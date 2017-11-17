package com.carecloud.carepay.practice.library.payments.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 11/9/17
 */

public class IntegratedPaymentDeviceGroupPayload {

    @SerializedName("device_groups")
    private List<IntegratedPaymentDeviceGroup> deviceGroupList = new ArrayList<>();

    public List<IntegratedPaymentDeviceGroup> getDeviceGroupList() {
        return deviceGroupList;
    }

    public void setDeviceGroupList(List<IntegratedPaymentDeviceGroup> deviceGroupList) {
        this.deviceGroupList = deviceGroupList;
    }
}
