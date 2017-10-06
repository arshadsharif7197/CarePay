package com.carecloud.carepaylibray.payments.models.history;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 9/28/17
 */

public class PaymentHistoryItem {

    @SerializedName("metadata")
    private PaymentHistoryItemMetadata metadata = new PaymentHistoryItemMetadata();

    @SerializedName("payload")
    private PaymentHistoryItemPayload payload = new PaymentHistoryItemPayload();

    public PaymentHistoryItemMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PaymentHistoryItemMetadata metadata) {
        this.metadata = metadata;
    }

    public PaymentHistoryItemPayload getPayload() {
        return payload;
    }

    public void setPayload(PaymentHistoryItemPayload payload) {
        this.payload = payload;
    }
}
