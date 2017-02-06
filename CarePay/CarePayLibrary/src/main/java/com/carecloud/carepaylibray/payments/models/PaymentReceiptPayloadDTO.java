
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentReceiptPayloadDTO {

    @SerializedName("metadata")
    @Expose
    private PaymentPayloadMetaDataDTO metadata;

    public PaymentPayloadMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PaymentPayloadMetaDataDTO metadata) {
        this.metadata = metadata;
    }
}
