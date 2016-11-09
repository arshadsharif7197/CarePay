package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SelectLanguageDTO {
    @SerializedName("metadata")
    @Expose
    private MetaDataDTO metadata;
    @SerializedName("payload")
    @Expose
    private PayloadDTO payload;
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * @return The metadata
     */
    public MetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(MetaDataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public PayloadDTO getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(PayloadDTO payload) {
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
