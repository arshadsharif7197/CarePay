package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prem_mourya on 11/15/2016.
 */

public class PatientModeSwitchPinMetadataDTO {

    @SerializedName("user_id")
    @Expose
    private PatientModeUserIdDTO patientModeUserIdDTO;
    @SerializedName("username")
    @Expose
    private String username;

    /**
     *
     * @return
     * The patientModeUserIdDTO
     */
    public PatientModeUserIdDTO getPatientModeUserIdDTO() {
        return patientModeUserIdDTO;
    }

    /**
     *
     * @param patientModeUserIdDTO
     * The user_id
     */
    public void setPatientModeUserIdDTO(PatientModeUserIdDTO patientModeUserIdDTO) {
        this.patientModeUserIdDTO = patientModeUserIdDTO;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
