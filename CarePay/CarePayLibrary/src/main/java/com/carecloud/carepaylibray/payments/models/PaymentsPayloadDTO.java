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
    @SerializedName("payment_settings")
    @Expose
    private PaymentsPayloadSettingsDTO paymentSettings;
    @SerializedName("patient_payment_plans")
    @Expose
    private PaymentsPatientsPlansDTO patientPaymentPlans;
    @SerializedName("patient_history")
    @Expose
    private PaymentsPatientHistoryDTO patientHistory;
    @SerializedName("patient_credit_cards")
    @Expose
    private PaymentsPatientsCreditCardsPayloadDTO patientCreditCards;

    @SerializedName("provider_index")
    @Expose
    private List<ProviderIndexDTO> providerIndex;
    @SerializedName("location_index")
    @Expose
    private List<LocationIndexDTO> locationIndex;
    @SerializedName("in_office_counts")
    @Expose
    private Integer inOfficeCounts;
    @SerializedName("patient_balances")
    @Expose
    private List<PaymentsPatientBalancessDTO> patientBalances = new ArrayList<>();

    @SerializedName("providers")
    @Expose
    private List<ProviderDTO> providers = null;
    @SerializedName("locations")
    @Expose
    private List<LocationDTO> locations = null;

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

    public PaymentsPatientHistoryDTO getPatientHistory() {
        return patientHistory;
    }

    public void setPatientHistory(PaymentsPatientHistoryDTO patientHistory) {
        this.patientHistory = patientHistory;
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

    public List<ProviderIndexDTO> getProviderIndex() {
        return providerIndex;
    }

    public void setProviderIndex(List<ProviderIndexDTO> providerIndex) {
        this.providerIndex = providerIndex;
    }

    public List<LocationIndexDTO> getLocationIndex() {
        return locationIndex;
    }

    public void setLocationIndex(List<LocationIndexDTO> locationIndex) {
        this.locationIndex = locationIndex;
    }

    public Integer getInOfficeCounts() {
        return inOfficeCounts;
    }

    public void setInOfficeCounts(Integer inOfficeCounts) {
        this.inOfficeCounts = inOfficeCounts;
    }

    public List<ProviderDTO> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderDTO> providers) {
        this.providers = providers;
    }

    public List<LocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }
}
