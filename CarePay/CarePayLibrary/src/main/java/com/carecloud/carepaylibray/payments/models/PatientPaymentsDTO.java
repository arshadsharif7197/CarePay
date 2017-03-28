
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PatientPaymentsDTO {

    @SerializedName("payload")
    @Expose
    private List<PatientPaymentPayload> payload = new ArrayList<>();

    public List<PatientPaymentPayload> getPayload() {
        return payload;
    }

    public void setPayload(List<PatientPaymentPayload> payload) {
        this.payload = payload;
    }

}
