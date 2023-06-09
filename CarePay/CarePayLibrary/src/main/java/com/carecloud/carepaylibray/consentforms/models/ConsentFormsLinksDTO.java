package com.carecloud.carepaylibray.consentforms.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 19/03/18.
 */

public class ConsentFormsLinksDTO {

    @Expose
    @SerializedName("appointments")
    private TransitionDTO appointments = new TransitionDTO();
    @Expose
    @SerializedName("consent_forms")
    private TransitionDTO consentForms = new TransitionDTO();
    @Expose
    @SerializedName("demographics")
    private TransitionDTO demographics = new TransitionDTO();
    @Expose
    @SerializedName("forms_history")
    private TransitionDTO formsHistory = new TransitionDTO();
    @Expose
    @SerializedName("patient_balances")
    private TransitionDTO patientBalances = new TransitionDTO();
    @Expose
    @SerializedName(value = "update_forms", alternate = "update_pending_forms")
    private TransitionDTO updateForms;
    @Expose
    @SerializedName("self")
    private TransitionDTO self;
    @Expose
    @SerializedName("history_forms")
    private TransitionDTO historyForms;

    public TransitionDTO getAppointments() {
        return appointments;
    }

    public void setAppointments(TransitionDTO appointments) {
        this.appointments = appointments;
    }

    public TransitionDTO getConsentForms() {
        return consentForms;
    }

    public void setConsentForms(TransitionDTO consentForms) {
        this.consentForms = consentForms;
    }

    public TransitionDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(TransitionDTO demographics) {
        this.demographics = demographics;
    }

    public TransitionDTO getFormsHistory() {
        return formsHistory;
    }

    public void setFormsHistory(TransitionDTO formsHistory) {
        this.formsHistory = formsHistory;
    }

    public TransitionDTO getPatientBalances() {
        return patientBalances;
    }

    public void setPatientBalances(TransitionDTO patientBalances) {
        this.patientBalances = patientBalances;
    }

    public TransitionDTO getUpdateForms() {
        return updateForms;
    }

    public void setUpdateForms(TransitionDTO updateForms) {
        this.updateForms = updateForms;
    }

    public TransitionDTO getSelf() {
        return self;
    }

    public void setSelf(TransitionDTO self) {
        this.self = self;
    }

    public TransitionDTO getHistoryForms() {
        return historyForms;
    }

    public void setHistoryForms(TransitionDTO historyForms) {
        this.historyForms = historyForms;
    }
}
