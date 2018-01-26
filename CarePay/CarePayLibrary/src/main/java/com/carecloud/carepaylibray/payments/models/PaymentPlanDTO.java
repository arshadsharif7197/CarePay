package com.carecloud.carepaylibray.payments.models;

/**
 * Created by Rahul on 11/30/16
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentPlanDTO extends PaymentListItem {

    @SerializedName("metadata")
    @Expose
    private PaymentPlanMetadataDTO metadata = new PaymentPlanMetadataDTO();
    @SerializedName("payload")
    @Expose
    private PaymentPlanPayloadDTO payload = new PaymentPlanPayloadDTO();


    public PaymentPlanMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PaymentPlanMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public PaymentPlanPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(PaymentPlanPayloadDTO payload) {
        this.payload = payload;
    }
}


