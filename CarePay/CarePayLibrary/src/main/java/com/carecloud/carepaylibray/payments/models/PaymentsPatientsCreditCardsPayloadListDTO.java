package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PaymentsPatientsCreditCardsPayloadListDTO {

    @SerializedName("payload")
    @Expose
    private PaymentCreditCardsPayloadDTO payload;

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
