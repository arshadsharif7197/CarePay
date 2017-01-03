
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatienceBalanceDTO {

    @SerializedName("metadata")
    @Expose
    private PaymentPayloadMetaDataDTO metadata;
    @SerializedName("payload")
    @Expose
    private PatienceBalancePayloadDTO payload;

    public PaymentPayloadMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PaymentPayloadMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    public PatienceBalancePayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(PatienceBalancePayloadDTO payload) {
        this.payload = payload;
    }

}
