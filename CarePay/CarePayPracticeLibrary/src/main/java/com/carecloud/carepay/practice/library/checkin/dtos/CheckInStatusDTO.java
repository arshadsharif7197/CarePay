package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhir_pingale on 11/11/2016.
 */

public class CheckInStatusDTO {
    @SerializedName("metadata")
    @Expose
    private CheckInMetadataDTO metadata = new CheckInMetadataDTO();
    @SerializedName("payload")
    @Expose
    private CheckInStatusPayloadDTO payload = new CheckInStatusPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * @return The metadata
     */
    public CheckInMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(CheckInMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public CheckInStatusPayloadDTO getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(CheckInStatusPayloadDTO payload) {
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
