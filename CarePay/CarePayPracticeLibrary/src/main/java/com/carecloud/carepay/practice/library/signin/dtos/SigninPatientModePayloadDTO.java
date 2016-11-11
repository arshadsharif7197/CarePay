package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by prem_mourya on 11/11/2016.
 */

public class SigninPatientModePayloadDTO {

    @SerializedName("patient_mode_signin")
    @Expose
    private SigninPayloadPatientModeDTO patientModeSignin;

    /**
     *
     * @return
     * The patientModeSignin
     */
    public SigninPayloadPatientModeDTO getPatientModeSignin() {
        return patientModeSignin;
    }

    /**
     *
     * @param patientModeSignin
     * The patient_mode_signin
     */
    public void setPatientModeSignin(SigninPayloadPatientModeDTO patientModeSignin) {
        this.patientModeSignin = patientModeSignin;
    }
}
