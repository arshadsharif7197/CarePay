package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPatientsCreditCardsPayloadDTO {


    @SerializedName("metadata")
    @Expose
    private PaymentPatientPlansPayloadDTO metadata;
    @SerializedName("payload")
    @Expose
    private List<PaymentCreditCardsPayloadDTO> payload = new ArrayList<PaymentCreditCardsPayloadDTO>();

    /**
     *
     * @return
     * The metadata
     */
    public PaymentPatientPlansPayloadDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PaymentPatientPlansPayloadDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public List<PaymentCreditCardsPayloadDTO> getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(List<PaymentCreditCardsPayloadDTO> payload) {
        this.payload = payload;
    }
}
