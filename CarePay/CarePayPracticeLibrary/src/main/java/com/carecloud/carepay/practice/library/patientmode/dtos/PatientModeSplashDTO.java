package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PatientModeSplashDTO {

    @SerializedName("metadata")
    @Expose
    private PatientModeMetaDataDTO metadata = new PatientModeMetaDataDTO();
    @SerializedName("payload")
    @Expose
    private PatientModePayloadDTO payload = new PatientModePayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     *
     * @return
     * The metadata
     */
    public PatientModeMetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PatientModeMetaDataDTO metadata) {
        this.metadata = metadata;
    }


    /**
     *
     * @return
     * The payload
     */
    public PatientModePayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(PatientModePayloadDTO payload) {
        this.payload = payload;
    }


    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }

}