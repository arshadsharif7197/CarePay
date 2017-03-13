
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentReceiptPayloadDTO {

    @SerializedName("metadata")
    @Expose
    private XPendingBalanceMetadataDTO metadata = new XPendingBalanceMetadataDTO();

    public XPendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(XPendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }
}
