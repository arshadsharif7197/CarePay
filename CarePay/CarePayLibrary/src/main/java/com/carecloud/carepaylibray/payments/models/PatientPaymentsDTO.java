
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PatientPaymentsDTO {

    @SerializedName("payload")
    @Expose
    private List<PaymentReceiptPayloadDTO> payload = new ArrayList<>();

    public List<PaymentReceiptPayloadDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<PaymentReceiptPayloadDTO> payload) {
        this.payload = payload;
    }

}
