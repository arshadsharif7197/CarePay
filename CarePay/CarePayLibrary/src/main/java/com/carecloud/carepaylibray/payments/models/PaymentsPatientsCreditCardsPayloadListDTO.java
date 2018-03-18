package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsPatientsCreditCardsPayloadListDTO {

    @SerializedName("payload")
    @Expose
    private PaymentCreditCardsPayloadDTO payload = new PaymentCreditCardsPayloadDTO();

    /**
     * @return payload
     */
    public PaymentCreditCardsPayloadDTO getPayload() {
        return payload;
    }

    /**
     * @param payload payload
     */
    public void setPayload(PaymentCreditCardsPayloadDTO payload) {
        this.payload = payload;
    }
}
