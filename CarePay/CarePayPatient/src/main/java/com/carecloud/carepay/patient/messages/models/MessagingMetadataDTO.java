package com.carecloud.carepay.patient.messages.models;

import com.google.gson.annotations.SerializedName;

public class MessagingMetadataDTO {

    @SerializedName("links")
    private MessagingMetadataLinksDTO links = new MessagingMetadataLinksDTO();

    public MessagingMetadataLinksDTO getLinks() {
        return links;
    }

    public void setLinks(MessagingMetadataLinksDTO links) {
        this.links = links;
    }
}
