package com.carecloud.carepay.mini.services;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kkannan on 5/24/17
 */

public class ServiceResponseDTO {

    @SerializedName("metadata")
    private JsonObject metadata;

    @SerializedName("payload")
    private JsonObject payload;

    @SerializedName("state")
    private String state;

    public JsonObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
