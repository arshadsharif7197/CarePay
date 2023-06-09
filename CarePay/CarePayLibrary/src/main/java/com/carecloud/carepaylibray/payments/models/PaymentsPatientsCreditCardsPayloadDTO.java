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
    private PaymentPlanDetailsDTO metadata = new PaymentPlanDetailsDTO();
    @SerializedName("payload")
    @Expose
    private List<PaymentsPatientsCreditCardsPayloadListDTO> payload = new ArrayList<>();

    /**
     *
     * @return
     * The metadata
     */
    public PaymentPlanDetailsDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PaymentPlanDetailsDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public List<PaymentsPatientsCreditCardsPayloadListDTO> getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(List<PaymentsPatientsCreditCardsPayloadListDTO> payload) {
        this.payload = payload;
    }
}
