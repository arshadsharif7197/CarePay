
package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SigninPatientModeDTO {

    @SerializedName("metadata")
    @Expose
    private SigninPatientModeMetadataDTO metadata;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("payload")
    @Expose
    private SigninPatientModePayloadDTO payload;

    /**
     * 
     * @return
     *     The metadata
     */
    public SigninPatientModeMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(SigninPatientModeMetadataDTO metadata) {
        this.metadata = metadata;
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

    public SigninPatientModePayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(SigninPatientModePayloadDTO payload) {
        this.payload = payload;
    }
}
