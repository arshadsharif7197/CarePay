package com.carecloud.carepaylibray.payments.models.updatebalance;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/22/17.
 */

public class UpdatePatientPaymentsDTO {

    @SerializedName("payload")
    private UpdatePatientBalancesPayload payload;

    public UpdatePatientBalancesPayload getPayload() {
        return payload;
    }

    public void setPayload(UpdatePatientBalancesPayload payload) {
        this.payload = payload;
    }

}
