package com.carecloud.carepaylibray.intake.models;

import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PayloadModel {

    @SerializedName("payments")
    @Expose
    private PaymentsModel payments = new PaymentsModel();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentPayloadModel> appointments = new ArrayList<>();
    @SerializedName("intake_forms")
    @Expose
    private List<IntakeForm> intakeForms = new ArrayList<>();
    @SerializedName("findings")
    @Expose
    private IntakeFindings findings = new IntakeFindings();
    @Expose
    @SerializedName("checkout_forms_user_response")
    private List<ConsentFormUserResponseDTO> responses = new ArrayList<>();

    /**
     * @return The payments
     */
    public PaymentsModel getPayments() {
        return payments;
    }

    /**
     * @param payments The payments
     */
    public void setPayments(PaymentsModel payments) {
        this.payments = payments;
    }

    /**
     * @return The appointments
     */
    public List<AppointmentPayloadModel> getAppointments() {
        return appointments;
    }

    /**
     * @param appointments The appointments
     */
    public void setAppointments(List<AppointmentPayloadModel> appointments) {
        this.appointments = appointments;
    }

    /**
     * @return The intakeForms
     */
    public List<IntakeForm> getIntakeForms() {
        return intakeForms;
    }

    /**
     * @param intakeForms The intake_forms
     */
    public void setIntakeForms(List<IntakeForm> intakeForms) {
        this.intakeForms = intakeForms;
    }

    /**
     * @return The findings
     */
    public IntakeFindings getFindings() {
        return findings;
    }

    /**
     * @param findings The findings
     */
    public void setFindings(IntakeFindings findings) {
        this.findings = findings;
    }

    public List<ConsentFormUserResponseDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<ConsentFormUserResponseDTO> responses) {
        this.responses = responses;
    }
}
