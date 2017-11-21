package com.carecloud.carepay.patient.retail.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailModel implements DTO {

    @SerializedName("metadata")
    private RetailMetadataDTO metadata = new RetailMetadataDTO();

    @SerializedName("payload")
    private RetailPayloadDTO payload;

    @SerializedName("state")
    private String state;

    public RetailMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(RetailMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public RetailPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(RetailPayloadDTO payload) {
        this.payload = payload;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
