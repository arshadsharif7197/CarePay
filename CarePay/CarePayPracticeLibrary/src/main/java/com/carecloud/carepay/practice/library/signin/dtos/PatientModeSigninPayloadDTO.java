
package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/17/2016.
 */

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeSigninPayloadDTO {

    @SerializedName("patient_mode_signin")
    @Expose
    private PatientModeSigninDataDTO patientModeSigninData;

    @SerializedName("login")
    @Expose
    private PatientModeLoginDataDTO payload;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("personal_info_check")
    @Expose
    private PatientModePersonalInfoCheckDTO patientModePersonalInfoCheck;

    /**
     * 
     * @return
     *     The patientModeSigninData
     */
    public PatientModeSigninDataDTO getPatientModeSigninData() {
        return patientModeSigninData;
    }

    /**
     * 
     * @param patientModeSigninData
     *     The patient_mode_signin_data
     */
    public void setPatientModeSigninData(PatientModeSigninDataDTO patientModeSigninData) {
        this.patientModeSigninData = patientModeSigninData;
    }

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets patient mode login data.
     *
     * @return the patient mode login data
     */
    public PatientModeLoginDataDTO getPatientModeLoginData() {
        return payload;
    }

    /**
     * Sets patient mode login data.
     *
     * @param payload the payload
     */
    public void setPatientModeLoginData(PatientModeLoginDataDTO payload) {
        this.payload = payload;
    }


    /**
     * Gets patient mode personal info check.
     *
     * @return the patient mode personal info check
     */
    public PatientModePersonalInfoCheckDTO getPatientModePersonalInfoCheck() {
        return patientModePersonalInfoCheck;
    }

    /**
     * Sets patient mode personal info check.
     *
     * @param patientModePersonalInfoCheck the patient mode personal info check
     */
    public void setPatientModePersonalInfoCheck(PatientModePersonalInfoCheckDTO patientModePersonalInfoCheck) {
        this.patientModePersonalInfoCheck = patientModePersonalInfoCheck;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return  gson.toJson(this);
    }
}
