
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientPaymentsDTO {

    @SerializedName("payload")
    @Expose
    private PatientPaymentPayload payload = new PatientPaymentPayload();


    public PatientPaymentPayload getPayload() {
        return payload;
    }

    public void setPayload(PatientPaymentPayload payload) {
        this.payload = payload;
    }
}
