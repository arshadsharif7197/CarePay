
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentReceiptPayloadDTO {

    @SerializedName("metadata")
    @Expose
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();
    @SerializedName("payload")
    @Expose
    private PatientPaymentPayload payload = new PatientPaymentPayload();


    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public PatientPaymentPayload getPayload() {
        return payload;
    }

    public void setPayload(PatientPaymentPayload payload) {
        this.payload = payload;
    }
}
