package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SignInPayloadDTO {

    @SerializedName("patient_app_signin")
    @Expose
    private PatientAppSigninDTO patientAppSignin;

    /**
     * @return The patientAppSignin
     */
    public PatientAppSigninDTO getPatientAppSignin() {
        return patientAppSignin;
    }

    /**
     * @param patientAppSignin The patient_app_signin
     */
    public void setPatientAppSignin(PatientAppSigninDTO patientAppSignin) {
        this.patientAppSignin = patientAppSignin;
    }

}