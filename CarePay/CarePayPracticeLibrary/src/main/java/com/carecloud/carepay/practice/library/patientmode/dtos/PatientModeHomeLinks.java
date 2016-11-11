
package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeHomeLinks {

    @SerializedName("self")
    @Expose
    private TransitionDTO self;
    @SerializedName("pinpad")
    @Expose
    private TransitionDTO pinpad;

    /**
     * 
     * @return
     *     The self
     */
    public TransitionDTO getSelf() {
        return self;
    }

    /**
     * 
     * @param self
     *     The self
     */
    public void setSelf(TransitionDTO self) {
        this.self = self;
    }

    /**
     * 
     * @return
     *     The pinpad
     */
    public TransitionDTO getPinpad() {
        return pinpad;
    }

    /**
     * 
     * @param pinpad
     *     The pinpad
     */
    public void setPinpad(TransitionDTO pinpad) {
        this.pinpad = pinpad;
    }

}
