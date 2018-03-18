package com.carecloud.carepaylibray.payments.models.updatebalance;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/22/17.
 */

public class UpdatePatientPaymentsDTO {

    @SerializedName("payload")
    private List<UpdatePatientBalancesPayload> payload = new ArrayList<>();


    public List<UpdatePatientBalancesPayload> getPayload() {
        return payload;
    }

    public void setPayload(List<UpdatePatientBalancesPayload> payload) {
        this.payload = payload;
    }
}
