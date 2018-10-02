package com.carecloud.carepaylibray.base.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class LatestVersionDTO {

    @SerializedName("metadata")
    private JsonElement metadata;

    @SerializedName("payload")
    private LatestVersionPayloadDTO payload = new LatestVersionPayloadDTO();

    public JsonElement getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonElement metadata) {
        this.metadata = metadata;
    }

    public LatestVersionPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(LatestVersionPayloadDTO payload) {
        this.payload = payload;
    }
}
