
package com.carecloud.carepay.practice.library.signin.dtos;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SigninPatientModeQueryStringDTO {

    @SerializedName("practice_mgmt")
    @Expose
    private SigninPatientModeNameValidationDTO practiceMgmt = new SigninPatientModeNameValidationDTO();
    @SerializedName("practice_id")
    @Expose
    private SigninPatientModeNameValidationDTO practiceId = new SigninPatientModeNameValidationDTO();
    @SerializedName("patient_id")
    @Expose
    private SigninPatientModeNameValidationDTO patientId = new SigninPatientModeNameValidationDTO();

    /**
     * 
     * @return
     *     The practiceMgmt
     */
    public SigninPatientModeNameValidationDTO getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     * 
     * @param practiceMgmt
     *     The practice_mgmt
     */
    public void setPracticeMgmt(SigninPatientModeNameValidationDTO practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     * 
     * @return
     *     The practiceId
     */
    public SigninPatientModeNameValidationDTO getPracticeId() {
        return practiceId;
    }

    /**
     * 
     * @param practiceId
     *     The practice_id
     */
    public void setPracticeId(SigninPatientModeNameValidationDTO practiceId) {
        this.practiceId = practiceId;
    }

    /**
     * 
     * @return
     *     The patientId
     */
    public SigninPatientModeNameValidationDTO getPatientId() {
        return patientId;
    }

    /**
     * 
     * @param patientId
     *     The patient_id
     */
    public void setPatientId(SigninPatientModeNameValidationDTO patientId) {
        this.patientId = patientId;
    }

}
