
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PatienceBalanceDTO {

    @SerializedName("metadata")
    @Expose
    private PaymentPayloadMetaDataDTO metadata;

    @SerializedName("payload")
    @Expose
    private List<PatiencePayloadDTO> payload = null;

    public PaymentPayloadMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PaymentPayloadMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    public List<PatiencePayloadDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<PatiencePayloadDTO> payload) {
        this.payload = payload;
    }


}
