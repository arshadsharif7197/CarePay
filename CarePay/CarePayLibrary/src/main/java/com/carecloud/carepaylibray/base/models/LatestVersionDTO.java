package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

public class LatestVersionDTO {

    @SerializedName("metadata")
    private LatestVersionMetadataDTO metadata = new LatestVersionMetadataDTO();

    @SerializedName("payload")
    private LatestVersionPayloadDTO payload = new LatestVersionPayloadDTO();

    public LatestVersionMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(LatestVersionMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public LatestVersionPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(LatestVersionPayloadDTO payload) {
        this.payload = payload;
    }
}
