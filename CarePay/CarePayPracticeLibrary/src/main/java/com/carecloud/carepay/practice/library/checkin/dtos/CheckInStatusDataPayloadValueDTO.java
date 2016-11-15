package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhir_pingale on 11/11/2016.
 */

public class CheckInStatusDataPayloadValueDTO {

    @SerializedName("intake_forms_complete")
    @Expose
    private boolean intakeFormsComplete;

    @SerializedName("demographics_verify_complete")
    @Expose
    private boolean demographicsVerifyComplete;

    @SerializedName("consent_forms_complete")
    @Expose
    private boolean consentFormsComplete;

    @SerializedName("respsonsibility")
    @Expose
    private boolean respsonsibility;

    /**
     * @return The intakeFormsComplete
     */
    public boolean getIntakeFormsComplete() {
        return intakeFormsComplete;
    }

    /**
     * @param intakeFormsComplete The intake_forms_complete
     */
    public void setIntakeFormsComplete(boolean intakeFormsComplete) {
        this.intakeFormsComplete = intakeFormsComplete;
    }

    /**
     * @return The demographicsVerifyComplete
     */
    public boolean getDemographicsVerifyComplete() {
        return demographicsVerifyComplete;
    }

    /**
     * @param demographicsVerifyComplete The demographics_verify_complete
     */
    public void setDemographicsVerifyComplete(boolean demographicsVerifyComplete) {
        this.demographicsVerifyComplete = demographicsVerifyComplete;
    }

    /**
     * @return The consentFormsComplete
     */
    public boolean getConsentFormsComplete() {
        return consentFormsComplete;
    }

    /**
     * @param consentFormsComplete The consent_forms_complete
     */
    public void setConsentFormsComplete(boolean consentFormsComplete) {
        this.consentFormsComplete = consentFormsComplete;
    }

    /**
     * @return The respsonsibility
     */
    public boolean getRespsonsibility() {
        return respsonsibility;
    }

    /**
     * @param respsonsibility The respsonsibility
     */
    public void setRespsonsibility(boolean respsonsibility) {
        this.respsonsibility = respsonsibility;
    }
}
