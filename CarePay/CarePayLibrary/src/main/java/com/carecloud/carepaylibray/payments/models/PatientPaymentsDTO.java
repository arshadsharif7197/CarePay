
package com.carecloud.carepaylibray.payments.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientPaymentsDTO {

    @SerializedName("payload")
    @Expose
    private List<PaymentReceiptPayloadDTO> payload = null;

    public List<PaymentReceiptPayloadDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<PaymentReceiptPayloadDTO> payload) {
        this.payload = payload;
    }

}
