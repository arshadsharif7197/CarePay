package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeLinksDTO {

    @SerializedName("self")
    @Expose
    private PatientModesSelfDTO self = new PatientModesSelfDTO();
    @SerializedName("pinpad")
    @Expose
    private TransitionDTO pinpad = new TransitionDTO();

    /**
     * @return The self
     */
    public PatientModesSelfDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(PatientModesSelfDTO self) {
        this.self = self;
    }

    public TransitionDTO getPinpad() {
        return pinpad;
    }

    public void setPinpad(TransitionDTO pinpad) {
        this.pinpad = pinpad;
    }
}