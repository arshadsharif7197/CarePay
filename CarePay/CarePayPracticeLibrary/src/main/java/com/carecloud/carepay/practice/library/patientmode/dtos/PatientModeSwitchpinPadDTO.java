package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prem_mourya on 11/15/2016.
 */

public class PatientModeSwitchpinPadDTO {


    @SerializedName("payload")
    @Expose
    private Boolean payload;
    @SerializedName("metadata")
    @Expose
    private PatientModeSwitchPinMetadataDTO metadata = new PatientModeSwitchPinMetadataDTO();

    /**
     *
     * @return
     * The payload
     */
    public Boolean getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(Boolean payload) {
        this.payload = payload;
    }

    /**
     *
     * @return
     * The metadata
     */
    public PatientModeSwitchPinMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PatientModeSwitchPinMetadataDTO metadata) {
        this.metadata = metadata;
    }

}
