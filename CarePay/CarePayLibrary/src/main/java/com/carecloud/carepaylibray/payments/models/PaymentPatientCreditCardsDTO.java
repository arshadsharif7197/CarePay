
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PaymentPatientCreditCardsDTO {

    @SerializedName("payload")
    @Expose
    private List<PaymentCreditCardPayloadDTO> payload = new ArrayList<>();

    /**
     * @return The payload
     */
    public List<PaymentCreditCardPayloadDTO> getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(List<PaymentCreditCardPayloadDTO> payload) {
        this.payload = payload;
    }

}
