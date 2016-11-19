package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PayloadModel {

    @SerializedName("payments")
    @Expose
    private PaymentsModel payments;
    @SerializedName("appointments")
    @Expose
    private List<AppointmentPayloadModel> appointments = new ArrayList<>();
    @SerializedName("intake_forms")
    @Expose
    private List<IntakeFormPayloadModel> intakeForms = new ArrayList<IntakeFormPayloadModel>();
    @SerializedName("findings")
    @Expose
    private FindingsPayloadModel findings;


    /**
     * 
     * @return
     *     The payments
     */
    public PaymentsModel getPayments() {
        return payments;
    }

    /**
     * 
     * @param payments
     *     The payments
     */
    public void setPayments(PaymentsModel payments) {
        this.payments = payments;
    }

    /**
     *
     * @return
     *     The appointments
     */
    public List<AppointmentPayloadModel> getAppointments() {
        return appointments;
    }

    /**
     *
     * @param appointments
     *     The appointments
     */
    public void setAppointments(List<AppointmentPayloadModel> appointments) {
        this.appointments = appointments;
    }

    /**
     *
     * @return
     *     The intakeForms
     */
    public List<IntakeFormPayloadModel> getIntakeForms() {
        return intakeForms;
    }

    /**
     *
     * @param intakeForms
     *     The intake_forms
     */
    public void setIntakeForms(List<IntakeFormPayloadModel> intakeForms) {
        this.intakeForms = intakeForms;
    }

    /**
     *
     * @return
     *     The findings
     */
    public FindingsPayloadModel getFindings() {
        return findings;
    }

    /**
     *
     * @param findings
     *     The findings
     */
    public void setFindings(FindingsPayloadModel findings) {
        this.findings = findings;
    }


}
