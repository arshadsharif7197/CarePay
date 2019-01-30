package com.carecloud.carepay.patient.messages.models;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class AttachmentUploadModel {

    @SerializedName("nodeid")
    private String nodeId;

    @SerializedName("content_type")
    private String contentType;

    @SerializedName("url")
    private String url;

    @SerializedName("metadata")
    private JsonElement metadata;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public JsonElement getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonElement metadata) {
        this.metadata = metadata;
    }
}
