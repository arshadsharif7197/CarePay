package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prem_mourya on 11/15/2016.
 */

public class PatientModeSwitchPinPaylodDTO {
    @SerializedName("patient_mode_start")
    @Expose
    private PatientModesStartDTO patientModeStart = new PatientModesStartDTO();
    @SerializedName("pinpad")
    @Expose
    private PatientModeSwitchpinPadDTO pinpad = new PatientModeSwitchpinPadDTO();


    public PatientModesStartDTO getPatientModeStart() {
        return patientModeStart;
    }

    public void setPatientModeStart(PatientModesStartDTO patientModeStart) {
        this.patientModeStart = patientModeStart;
    }

    public PatientModeSwitchpinPadDTO getPinpad() {
        return pinpad;
    }

    public void setPinpad(PatientModeSwitchpinPadDTO pinpad) {
        this.pinpad = pinpad;
    }
}
