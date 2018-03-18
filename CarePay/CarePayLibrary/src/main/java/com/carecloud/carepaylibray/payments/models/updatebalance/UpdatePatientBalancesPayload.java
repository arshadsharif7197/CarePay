package com.carecloud.carepaylibray.payments.models.updatebalance;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/22/17.
 */

public class UpdatePatientBalancesPayload {

    @SerializedName("charge")
    private UpdatePatientBalancesCharge charge = new UpdatePatientBalancesCharge();

    public UpdatePatientBalancesCharge getCharge() {
        return charge;
    }

    public void setCharge(UpdatePatientBalancesCharge charge) {
        this.charge = charge;
    }
}
