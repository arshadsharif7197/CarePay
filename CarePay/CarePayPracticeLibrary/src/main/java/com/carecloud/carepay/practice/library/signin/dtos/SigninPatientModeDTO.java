
package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class SigninPatientModeDTO {

    @SerializedName("metadata")
    @Expose
    private SigninPatientModeMetadataDTO metadata = new SigninPatientModeMetadataDTO();
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("payload")
    @Expose
    private PatientModeSigninPayloadDTO payload = new PatientModeSigninPayloadDTO();

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

    public PatientModeSigninPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(PatientModeSigninPayloadDTO payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return  gson.toJson(this);
    }
}
