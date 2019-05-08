package com.carecloud.carepay.patient.messages.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.SerializedName;

public class MessagingModel implements DTO {

    @SerializedName("metadata")
    private MessagingMetadataDTO metadata = new MessagingMetadataDTO();

    @SerializedName("payload")
    private MessagingDataModel payload = new MessagingDataModel();

    @SerializedName("state")
    private String state;

    public MessagingMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(MessagingMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public MessagingDataModel getPayload() {
        return payload;
    }

    public void setPayload(MessagingDataModel payload) {
        this.payload = payload;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
