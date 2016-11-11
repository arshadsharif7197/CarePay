
package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeHomeDTO {

    @SerializedName("metadata")
    @Expose
    private PatientModeHomeMetadataDTO metadata;
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * 
     * @return
     *     The metadata
     */
    public PatientModeHomeMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(PatientModeHomeMetadataDTO metadata) {
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

}
