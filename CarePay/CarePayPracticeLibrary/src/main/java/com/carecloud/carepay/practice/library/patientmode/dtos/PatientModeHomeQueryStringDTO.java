
package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PatientModeHomeQueryStringDTO {

    @SerializedName("pin")
    @Expose
    private PatientModeHomeNameValidationDTO pin = new PatientModeHomeNameValidationDTO();

    /**
     * 
     * @return
     *     The pin
     */
    public PatientModeHomeNameValidationDTO getPin() {
        return pin;
    }

    /**
     * 
     * @param pin
     *     The pin
     */
    public void setPin(PatientModeHomeNameValidationDTO pin) {
        this.pin = pin;
    }

}
