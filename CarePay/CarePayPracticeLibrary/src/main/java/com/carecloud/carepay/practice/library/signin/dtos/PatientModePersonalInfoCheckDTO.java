package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/18/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class PatientModePersonalInfoCheckDTO {

    @SerializedName("metadata")
    @Expose
    private PatientModePersonalInfoCheckMetadataDTO metadata = new PatientModePersonalInfoCheckMetadataDTO();
    @SerializedName("payload")
    @Expose
    private boolean isPersonalInfoCheckSuccessful;

    /**
     *
     * @return
     * The metadata
     */
    public PatientModePersonalInfoCheckMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PatientModePersonalInfoCheckMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The isPersonalInfoCheckSuccessful
     */
    public boolean getPersonalInfoCheckSuccessful() {
        return isPersonalInfoCheckSuccessful;
    }

    /**
     *
     * @param isPersonalInfoCheckSuccessful
     * The is_personal_info_check_successful
     */
    public void setPersonalInfoCheckSuccessful(boolean isPersonalInfoCheckSuccessful) {
        this.isPersonalInfoCheckSuccessful = isPersonalInfoCheckSuccessful;
    }
}
