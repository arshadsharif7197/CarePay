package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhir_pingale on 11/11/2016
 */

public class CheckInStatusDataPayloadValueDTO {

    @SerializedName("intake_forms_complete")
    @Expose
    private String intakeFormsComplete;

    @SerializedName("demographics_verify_complete")
    @Expose
    private String demographicsVerifyComplete;

    @SerializedName("consent_forms_complete")
    @Expose
    private String consentFormsComplete;

    @SerializedName("respsonsibility")
    @Expose
    private String respsonsibility;

    @SerializedName("medications_allergies_complete")
    @Expose
    private String medicationsComplete;

    /**
     * @return The intakeFormsComplete
     */
    public String getIntakeFormsComplete() {
        return intakeFormsComplete;
    }

    /**
     * @param intakeFormsComplete The intake_forms_complete
     */
    public void setIntakeFormsComplete(String intakeFormsComplete) {
        this.intakeFormsComplete = intakeFormsComplete;
    }

    /**
     * @return The demographicsVerifyComplete
     */
    public String getDemographicsVerifyComplete() {
        return demographicsVerifyComplete;
    }

    /**
     * @param demographicsVerifyComplete The demographics_verify_complete
     */
    public void setDemographicsVerifyComplete(String demographicsVerifyComplete) {
        this.demographicsVerifyComplete = demographicsVerifyComplete;
    }

    /**
     * @return The consentFormsComplete
     */
    public String getConsentFormsComplete() {
        return consentFormsComplete;
    }

    /**
     * @param consentFormsComplete The consent_forms_complete
     */
    public void setConsentFormsComplete(String consentFormsComplete) {
        this.consentFormsComplete = consentFormsComplete;
    }

    /**
     * @return The respsonsibility
     */
    public String getRespsonsibility() {
        return respsonsibility;
    }

    /**
     * @param respsonsibility The respsonsibility
     */
    public void setRespsonsibility(String respsonsibility) {
        this.respsonsibility = respsonsibility;
    }

    public String getMedicationsComplete() {
        return medicationsComplete;
    }

    public void setMedicationsComplete(String medicationsComplete) {
        this.medicationsComplete = medicationsComplete;
    }
}
