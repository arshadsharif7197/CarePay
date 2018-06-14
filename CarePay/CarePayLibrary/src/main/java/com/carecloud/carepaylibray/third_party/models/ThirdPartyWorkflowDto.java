package com.carecloud.carepaylibray.third_party.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.SerializedName;

public class ThirdPartyWorkflowDto implements DTO {

    @SerializedName("metadata")
    private ThirdPartyMetadataModel metadata = new ThirdPartyMetadataModel();

    @SerializedName("payload")
    private ThirdPartyPayloadDto payload = new ThirdPartyPayloadDto();

    @SerializedName("state")
    private String state;

    public ThirdPartyMetadataModel getMetadata() {
        return metadata;
    }

    public void setMetadata(ThirdPartyMetadataModel metadata) {
        this.metadata = metadata;
    }

    public ThirdPartyPayloadDto getPayload() {
        return payload;
    }

    public void setPayload(ThirdPartyPayloadDto payload) {
        this.payload = payload;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
