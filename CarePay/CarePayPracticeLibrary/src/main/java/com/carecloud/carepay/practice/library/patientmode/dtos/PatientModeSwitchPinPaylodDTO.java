package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prem_mourya on 11/15/2016
 */

public class PatientModeSwitchPinPaylodDTO {
    @SerializedName("patient_mode_start")
    @Expose
    private PatientModesStartDTO patientModeStart = new PatientModesStartDTO();
    @SerializedName("pinpad")
    @Expose
    private PatientModeSwitchpinPadDTO pinpad = new PatientModeSwitchpinPadDTO();

    @SerializedName("user_practices")
    @Expose
    private List<PracticeSelectionUserPractice> userPracticesList = new ArrayList<>();

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

    public List<PracticeSelectionUserPractice> getUserPracticesList() {
        return userPracticesList;
    }

    public void setUserPracticesList(List<PracticeSelectionUserPractice> userPracticesList) {
        this.userPracticesList = userPracticesList;
    }
}
