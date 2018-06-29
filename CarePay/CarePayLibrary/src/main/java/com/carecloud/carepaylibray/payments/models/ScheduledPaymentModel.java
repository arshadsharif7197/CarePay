package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.SerializedName;

public class ScheduledPaymentModel {

    @SerializedName("metadata")
    private ScheduledPaymentMetadata metadata = new ScheduledPaymentMetadata();

    @SerializedName("payload")
    private ScheduledPaymentPayload payload = new ScheduledPaymentPayload();

    public ScheduledPaymentMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(ScheduledPaymentMetadata metadata) {
        this.metadata = metadata;
    }

    public ScheduledPaymentPayload getPayload() {
        return payload;
    }

    public void setPayload(ScheduledPaymentPayload payload) {
        this.payload = payload;
    }
}
