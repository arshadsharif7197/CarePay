
package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PendingBalanceDTO {

    @SerializedName("metadata")
    @Expose
    private PendingBalanceMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private List<PendingBalancePayloadDTO> payload = null;

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

}
