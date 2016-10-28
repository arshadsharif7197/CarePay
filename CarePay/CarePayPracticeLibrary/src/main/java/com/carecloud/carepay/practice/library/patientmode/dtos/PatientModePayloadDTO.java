package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/26/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModePayloadDTO {

    @SerializedName("patient_mode_start")
    @Expose
    private PatientModesStartDTO patientModeStart;

    /**
     * @return The patientModeStart
     */
    public PatientModesStartDTO getPatientModeStart() {
        return patientModeStart;
    }

    /**
     * @param patientModeStart The patient_mode_start
     */
    public void setPatientModeStart(PatientModesStartDTO patientModeStart) {
        this.patientModeStart = patientModeStart;
    }

}