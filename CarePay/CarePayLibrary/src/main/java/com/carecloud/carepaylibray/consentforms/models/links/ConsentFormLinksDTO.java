package com.carecloud.carepaylibray.consentforms.models.links;

/**
 * Created by Rahul on 10/21/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ConsentFormLinksDTO {

    @SerializedName("self")
    @Expose
    private ConsentFormsSelfDTO self;
    @SerializedName("consent_forms")
    @Expose
    private ConsentForms consentForms;
    @SerializedName("demographics")
    @Expose
    private ConsentFormDemographicsDTO demographics;
    @SerializedName("appointments")
    @Expose
    private ConsentFormsAppointmentsDTO appointments;
    @SerializedName("patient_balances")
    @Expose
    private ConsentFormPatientBalancesDTO patientBalances;

    /**
     * @return The self
     */
    public ConsentFormsSelfDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(ConsentFormsSelfDTO self) {
        this.self = self;
    }

    /**
     * @return The consentForms
     */
    public ConsentForms getConsentForms() {
        return consentForms;
    }

    /**
     * @param consentForms The consent_forms
     */
    public void setConsentForms(ConsentForms consentForms) {
        this.consentForms = consentForms;
    }

    /**
     * @return The demographics
     */
    public ConsentFormDemographicsDTO getDemographics() {
        return demographics;
    }

    /**
     * @param demographics The demographics
     */
    public void setDemographics(ConsentFormDemographicsDTO demographics) {
        this.demographics = demographics;
    }

    /**
     * @return The appointments
     */
    public ConsentFormsAppointmentsDTO getAppointments() {
        return appointments;
    }

    /**
     * @param appointments The appointments
     */
    public void setAppointments(ConsentFormsAppointmentsDTO appointments) {
        this.appointments = appointments;
    }

    /**
     * @return The patientBalances
     */
    public ConsentFormPatientBalancesDTO getPatientBalances() {
        return patientBalances;
    }

    /**
     * @param patientBalances The patient_balances
     */
    public void setPatientBalances(ConsentFormPatientBalancesDTO patientBalances) {
        this.patientBalances = patientBalances;
    }

}