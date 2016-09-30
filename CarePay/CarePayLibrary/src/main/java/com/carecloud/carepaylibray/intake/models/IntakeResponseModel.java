package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IntakeResponseModel {

    @SerializedName("metadata")
    @Expose
    private Metadata metadata;
    @SerializedName("payload")
    @Expose
    private Payload payload;
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * 
     * @return
     *     The metadata
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public Payload getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }

}
