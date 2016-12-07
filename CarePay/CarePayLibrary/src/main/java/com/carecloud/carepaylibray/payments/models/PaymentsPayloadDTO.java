package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPayloadDTO implements Serializable {

    @SerializedName("intake_forms")
    @Expose
    private PaymentsPayloadIntakeFormsDTO intakeForms;
    @SerializedName("patient_balances")
    @Expose
    private List<PaymentsPatientBalancessDTO> patientBalances = new ArrayList<>();
    @SerializedName("payment_settings")
    @Expose
    private PaymentsPayloadSettingsDTO paymentSettings;
    @SerializedName("patient_payment_plans")
    @Expose
    private PaymentsPatientsPlansDTO patientPaymentPlans;
    @SerializedName("patient_credit_cards")
    @Expose
    private PaymentsPatientsCreditCardsPayloadDTO patientCreditCards;

    /**
     *
     * @return
     * The intakeForms
     */
    public PaymentsPayloadIntakeFormsDTO getIntakeForms() {
        return intakeForms;
    }

    /**
     *
     * @param intakeForms
     * The intake_forms
     */
    public void setIntakeForms(PaymentsPayloadIntakeFormsDTO intakeForms) {
        this.intakeForms = intakeForms;
    }

    /**
     *
     * @return
     * The paymentSettings
     */
    public PaymentsPayloadSettingsDTO getPaymentSettings() {
        return paymentSettings;
    }

    /**
     *
     * @param paymentSettings
     * The payment_settings
     */
    public void setPaymentSettings(PaymentsPayloadSettingsDTO paymentSettings) {
        this.paymentSettings = paymentSettings;
    }

    /**
     *
     * @return
     * The patientPaymentPlans
     */
    public PaymentsPatientsPlansDTO getPatientPaymentPlans() {
        return patientPaymentPlans;
    }

    /**
     *
     * @param patientPaymentPlans
     * The patient_payment_plans
     */
    public void setPatientPaymentPlans(PaymentsPatientsPlansDTO patientPaymentPlans) {
        this.patientPaymentPlans = patientPaymentPlans;
    }

    /**
     *
     * @return
     * The patientCreditCards
     */
    public PaymentsPatientsCreditCardsPayloadDTO getPatientCreditCards() {
        return patientCreditCards;
    }

    /**
     *
     * @param patientCreditCards
     * The patient_credit_cards
     */
    public void setPatientCreditCards(PaymentsPatientsCreditCardsPayloadDTO patientCreditCards) {
        this.patientCreditCards = patientCreditCards;
    }

    /**
     *
     * @return The patientBalances
     */
    public List<PaymentsPatientBalancessDTO> getPatientBalances() {
        return patientBalances;
    }

    /**
     *
     * @param patientBalances The patient_balances
     */
    public void setPatientBalances(List<PaymentsPatientBalancessDTO>patientBalances) {
        this.patientBalances = patientBalances;
    }

}
