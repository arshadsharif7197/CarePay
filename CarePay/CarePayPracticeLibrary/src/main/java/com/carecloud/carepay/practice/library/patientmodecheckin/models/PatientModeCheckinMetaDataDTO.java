package com.carecloud.carepay.practice.library.patientmodecheckin.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 4/27/17.
 */

public class PatientModeCheckinMetaDataDTO {

    @SerializedName("transitions")
    @Expose
    private PatientModeCheckinTransitionsDTO transitions = new PatientModeCheckinTransitionsDTO();

    public PatientModeCheckinTransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(PatientModeCheckinTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
