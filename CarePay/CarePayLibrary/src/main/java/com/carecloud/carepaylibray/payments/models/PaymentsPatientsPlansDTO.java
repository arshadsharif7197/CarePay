package com.carecloud.carepaylibray.payments.models;

/**
 * Created by Rahul on 11/30/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PaymentsPatientsPlansDTO {

    @SerializedName("metadata")
    @Expose
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();
    @SerializedName("payload")
    @Expose
    private List<PaymentPatientPlansPayloadDTO> payload = new ArrayList<PaymentPatientPlansPayloadDTO>();

    /**
     * @return The metadata
     */
    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public List<PaymentPatientPlansPayloadDTO> getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(List<PaymentPatientPlansPayloadDTO> payload) {
        this.payload = payload;
    }

}


