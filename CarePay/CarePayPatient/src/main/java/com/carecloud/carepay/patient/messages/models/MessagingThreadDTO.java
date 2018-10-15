package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

public class MessagingThreadDTO {

    @SerializedName("metadata")
    private MessagingMetadataDTO messagingMetadataDTO = new MessagingMetadataDTO();

    @SerializedName("payload")
    private Messages.Reply payload = new Messages.Reply();

    public MessagingMetadataDTO getMessagingMetadataDTO() {
        return messagingMetadataDTO;
    }

    public void setMessagingMetadataDTO(MessagingMetadataDTO messagingMetadataDTO) {
        this.messagingMetadataDTO = messagingMetadataDTO;
    }

    public Messages.Reply getPayload() {
        return payload;
    }

    public void setPayload(Messages.Reply payload) {
        this.payload = payload;
    }
}
