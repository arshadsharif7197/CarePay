package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPayloadSettingsDTO {

    @SerializedName("metadata")
    @Expose
    private PaymentPayloadMetaDataDTO metadata;
    @SerializedName("payload")
    @Expose
    private PaymentsSettingsPayloadDTO payload;

    /**
     *
     * @return
     * The metadata
     */
    public PaymentPayloadMetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PaymentPayloadMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public PaymentsSettingsPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(PaymentsSettingsPayloadDTO payload) {
        this.payload = payload;
    }
}
