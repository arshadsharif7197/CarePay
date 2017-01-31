package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */

import com.carecloud.carepaylibray.consentforms.models.payload.ConsentFormPayloadDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormDTO {

    @SerializedName("metadata")
    @Expose
    private ConsentFormMetadataDTO metadata;

    @SerializedName("payload")
    @Expose
    private ConsentFormPayloadDTO consentFormPayloadDTO;

    @SerializedName("state")
    @Expose
    private String state;

    /**
     * @return The metadata
     */
    public ConsentFormMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(ConsentFormMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public ConsentFormPayloadDTO getConsentFormPayloadDTO() {
        return consentFormPayloadDTO;
    }

    public void setConsentFormPayloadDTO(ConsentFormPayloadDTO consentFormPayloadDTO) {
        this.consentFormPayloadDTO = consentFormPayloadDTO;
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
