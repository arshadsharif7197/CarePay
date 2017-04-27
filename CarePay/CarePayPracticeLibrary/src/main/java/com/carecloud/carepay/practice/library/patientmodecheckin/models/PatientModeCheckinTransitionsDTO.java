package com.carecloud.carepay.practice.library.patientmodecheckin.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 4/27/17.
 */

public class PatientModeCheckinTransitionsDTO {

    @SerializedName("patient_home")
    @Expose
    private TransitionDTO patientHome = new TransitionDTO();

    public TransitionDTO getPatientHome() {
        return patientHome;
    }

    public void setPatientHome(TransitionDTO patientHome) {
        this.patientHome = patientHome;
    }
}
