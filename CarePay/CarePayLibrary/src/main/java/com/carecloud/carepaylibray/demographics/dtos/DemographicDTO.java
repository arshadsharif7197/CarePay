package com.carecloud.carepaylibray.demographics.dtos;

import com.carecloud.carepaylibray.demographics.dtos.metadata.DemographicMetadataModel;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Master DTO for demographics
 */
public class DemographicDTO {

    @SerializedName("metadata") @Expose private DemographicMetadataModel      metadata;
    @SerializedName("payload") @Expose private  DemographicPayloadResponseDTO payload;
    @SerializedName("state") @Expose private    String                        state;

    /**
     * @return The metadata
     */
    public DemographicMetadataModel getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(DemographicMetadataModel metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public DemographicPayloadResponseDTO getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(DemographicPayloadResponseDTO payload) {
        this.payload = payload;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }
}
