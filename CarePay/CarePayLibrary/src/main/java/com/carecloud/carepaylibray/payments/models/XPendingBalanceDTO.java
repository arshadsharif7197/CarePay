
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class XPendingBalanceDTO {

    @SerializedName("metadata")
    @Expose
    private XPendingBalanceMetadataDTO metadata = new XPendingBalanceMetadataDTO();

    @SerializedName("payload")
    @Expose
    private List<XPendingBalancePayloadDTO> payload = new ArrayList<>();

    public XPendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(XPendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public List<XPendingBalancePayloadDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<XPendingBalancePayloadDTO> payload) {
        this.payload = payload;
    }

    /**
     * Validate Metadata
     * @return true if metadata us valid
     */
    public boolean hasValidMetadata(){
        return metadata.isValid();
    }
}
