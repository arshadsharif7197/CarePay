
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientPaymentsDTO {

    @SerializedName("payload")
    @Expose
    private IntegratedPatientPaymentPayload payload = new IntegratedPatientPaymentPayload();

    public IntegratedPatientPaymentPayload getPayload() {
        return payload;
    }

    public void setPayload(IntegratedPatientPaymentPayload payload) {
        this.payload = payload;
    }
}
