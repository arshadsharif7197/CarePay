package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsMerchantServicesDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPapiAccountsDTO;
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
    private PaymentsPayloadIntakeFormsDTO intakeForms = new PaymentsPayloadIntakeFormsDTO();
    @SerializedName("payment_settings")
    @Expose
    private List<PaymentsPayloadSettingsDTO> paymentSettings = new ArrayList<>();
    @SerializedName("patient_payment_plans")
    @Expose
    private PaymentsPatientsPlansDTO patientPaymentPlans = new PaymentsPatientsPlansDTO();
    @SerializedName("patient_history")
    @Expose
    private PaymentsPatientHistoryDTO patientHistory = new PaymentsPatientHistoryDTO();
    @SerializedName("patient_credit_cards")
    @Expose
    private List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards = new ArrayList<>();
    @SerializedName("provider_index")
    @Expose
    private List<ProviderIndexDTO> providerIndex = new ArrayList<>();
    @SerializedName("location_index")
    @Expose
    private List<LocationIndexDTO> locationIndex = new ArrayList<>();
    @SerializedName("in_office_counts")
    @Expose
    private Integer inOfficeCounts;
    @SerializedName("patient_balances")
    @Expose
    private List<PatientBalanceDTO> patientBalances = new ArrayList<>();
    @SerializedName("providers")
    @Expose
    private List<ProviderDTO> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<LocationDTO> locations = new ArrayList<>();
    @SerializedName("patient_payments")
    @Expose
    private PatientPaymentsDTO patientPayments = new PatientPaymentsDTO();
    @SerializedName("patients")
    @Expose
    private List<PatientModel> patients = new ArrayList<>();
    @SerializedName("papi_accounts")
    @Expose
    private List<DemographicsSettingsPapiAccountsDTO> papiAccounts = new ArrayList<>();
    @SerializedName("merchant_services")
    @Expose
    private List<DemographicsSettingsMerchantServicesDTO> merchantServices = new ArrayList<>();

    public List<PatientModel> getPatients() {
        return patients;
    }

    public void setPatients(List<PatientModel> patients) {
        this.patients = patients;
    }

    public PatientPaymentsDTO getPatientPayments() {
        return patientPayments;
    }

    public void setPatientPayments(PatientPaymentsDTO patientPayments) {
        this.patientPayments = patientPayments;
    }

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
    public List<PaymentsPatientsCreditCardsPayloadListDTO> getPatientCreditCards() {
        return patientCreditCards;
    }

    /**
     *
     * @param patientCreditCards
     * The patient_credit_cards
     */
    public void setPatientCreditCards(List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards) {
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
    public List<PatientBalanceDTO> getPatientBalances() {
        return patientBalances;
    }

    /**
     *
     * @param patientBalances The patient_balances
     */
    public void setPatientBalances(List<PatientBalanceDTO>patientBalances) {
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

    /**
     * Gets papi accounts.
     *
     * @return the papi accounts
     */
    public List<DemographicsSettingsPapiAccountsDTO> getPapiAccounts() {
        return papiAccounts;
    }

    /**
     * Sets papi accounts.
     *
     * @param papiAccounts the papi accounts
     */
    public void setPapiAccounts(List<DemographicsSettingsPapiAccountsDTO> papiAccounts) {
        this.papiAccounts = papiAccounts;
    }

    /**
     * Gets merchant services.
     *
     * @return the merchant services
     */
    public List<DemographicsSettingsMerchantServicesDTO> getMerchantServices() {
        return merchantServices;
    }

    /**
     * Sets merchant services.
     *
     * @param merchantServices the merchant services
     */
    public void setMerchantServices(List<DemographicsSettingsMerchantServicesDTO> merchantServices) {
        this.merchantServices = merchantServices;
    }

    public List<PaymentsPayloadSettingsDTO> getPaymentSettings() {
        return paymentSettings;
    }

    public void setPaymentSettings(List<PaymentsPayloadSettingsDTO> paymentSettings) {
        this.paymentSettings = paymentSettings;
    }
}
