package com.carecloud.carepaylibray.demographics.dtos;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Master DTO for demographics
 */
public class DemographicDTO implements DTO{

    @SerializedName("metadata")
    @Expose
    private DemographicMetadataDTO metadata = new DemographicMetadataDTO();
    @SerializedName("payload")
    @Expose private
    DemographicPayloadResponseDTO payload = new DemographicPayloadResponseDTO();
    @SerializedName("state")
    @Expose private
    String state;

    /**
     * @return The metadata
     */
    public DemographicMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(DemographicMetadataDTO metadata) {
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
