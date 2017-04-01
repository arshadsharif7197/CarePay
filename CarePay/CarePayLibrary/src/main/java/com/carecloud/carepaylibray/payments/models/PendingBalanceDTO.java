
package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PendingBalanceDTO {

    @SerializedName("metadata")
    @Expose
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();

    @SerializedName("payload")
    @Expose
    private List<PendingBalancePayloadDTO> payload = new ArrayList<>();

    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public List<PendingBalancePayloadDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<PendingBalancePayloadDTO> payload) {
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
