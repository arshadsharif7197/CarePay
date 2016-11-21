package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/18/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeLoginDataDTO {
    @SerializedName("metadata")
    @Expose
    private PatientModeLoginDataMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private boolean isLoginSuccessful;

    /**
     *
     * @return
     * The metadata
     */
    public PatientModeLoginDataMetadataDTO getPatientModeLoginDataMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setPatientModeLoginDataMetadata(PatientModeLoginDataMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The isLoginSuccessful
     */
    public boolean getPayload() {
        return isLoginSuccessful;
    }

    /**
     *
     * @param isLoginSuccessful
     * The is_login_successful
     */
    public void setPayload(boolean isLoginSuccessful) {
        this.isLoginSuccessful = isLoginSuccessful;
    }
}
