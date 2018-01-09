package com.carecloud.carepaylibray.payments.models;


import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItemPayload;
import com.carecloud.carepaylibray.payments.models.history.PaymentsTransactionHistory;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPayloadDTO implements Serializable {


    @SerializedName("providers")
    @Expose
    private List<ProviderDTO> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<LocationDTO> locations = new ArrayList<>();
    @SerializedName("patient_balances")
    @Expose
    private List<PatientBalanceDTO> patientBalances = new ArrayList<>();
    @SerializedName(value = "user_practices", alternate = "practice_information")
    @Expose
    private List<UserPracticeDTO> userPractices = new ArrayList<>();
    @SerializedName("payment_settings")
    @Expose
    private List<PaymentsPayloadSettingsDTO> paymentSettings = new ArrayList<>();
    @SerializedName("merchant_services")
    @Expose
    private List<MerchantServicesDTO> merchantServices = new ArrayList<>();
    @SerializedName("patient_credit_cards")
    @Expose
    private List<PaymentsPatientsCreditCardsPayloadListDTO> patientCreditCards = new ArrayList<>();
    @SerializedName("patient_payment_plans")
    @Expose
    private PaymentsPatientsPlansDTO patientPaymentPlans = new PaymentsPatientsPlansDTO();
    @SerializedName("patient_history")
    @Expose
    private PaymentsPatientHistoryDTO patientHistory = new PaymentsPatientHistoryDTO();
    @SerializedName("provider_index")
    @Expose
    private List<ProviderIndexDTO> providerIndex = new ArrayList<>();
    @SerializedName("location_index")
    @Expose
    private List<LocationIndexDTO> locationIndex = new ArrayList<>();
    @SerializedName("in_office_counts")
    @Expose
    private Integer inOfficeCounts;
    @SerializedName("patient_payments")
    @Expose
    private PatientPaymentsDTO patientPayments = new PatientPaymentsDTO();
    @SerializedName("patients")
    @Expose
    private List<PatientModel> patients = new ArrayList<>();
    @SerializedName("papi_accounts")
    @Expose
    private List<PapiAccountsDTO> papiAccounts = new ArrayList<>();
    @SerializedName("payment_post_model")
    @Expose
    private IntegratedPaymentPostModel paymentPostModel;
    @SerializedName("simple_charge_types")
    @Expose
    private List<SimpleChargeItem> simpleChargeItems = new ArrayList<>();
    @SerializedName("transactions")
    @Expose
    private PaymentsTransactionHistory transactionHistory = new PaymentsTransactionHistory();
    @SerializedName("patient_refunds")
    @Expose
    private PaymentHistoryItemPayload patientRefund = new PaymentHistoryItemPayload();
    @SerializedName("payment_profile_id")
    @Expose
    private String paymentProfileId;
    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();
    @SerializedName("organization_id")
    private String organizationId;

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
     * @return The patientPaymentPlans
     */
    public PaymentsPatientsPlansDTO getPatientPaymentPlans() {
        return patientPaymentPlans;
    }

    /**
     * @param patientPaymentPlans The patient_payment_plans
     */
    public void setPatientPaymentPlans(PaymentsPatientsPlansDTO patientPaymentPlans) {
        this.patientPaymentPlans = patientPaymentPlans;
    }

    /**
     * @return The patientCreditCards
     */
    public List<PaymentsPatientsCreditCardsPayloadListDTO> getPatientCreditCards() {
        return patientCreditCards;
    }

    /**
     * @param patientCreditCards The patient_credit_cards
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
     * @return The patientBalances
     */
    public List<PatientBalanceDTO> getPatientBalances() {
        return patientBalances;
    }

    /**
     * @param patientBalances The patient_balances
     */
    public void setPatientBalances(List<PatientBalanceDTO> patientBalances) {
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
    public List<PapiAccountsDTO> getPapiAccounts() {
        return papiAccounts;
    }

    /**
     * Gets papi account by type.
     *
     * @return the papi account
     */
    public PapiAccountsDTO getPapiAccountByType(String accountType) {
        for (PapiAccountsDTO papiAccountDTO : getPapiAccounts()) {
            if (papiAccountDTO.getType().contains(accountType)) {
                return papiAccountDTO;
            }
        }
        return null;
    }

    /**
     * Sets papi accounts.
     *
     * @param papiAccounts the papi accounts
     */
    public void setPapiAccounts(List<PapiAccountsDTO> papiAccounts) {
        this.papiAccounts = papiAccounts;
    }

    /**
     * Gets merchant services.
     *
     * @return the merchant services
     */
    public List<MerchantServicesDTO> getMerchantServices() {
        return merchantServices;
    }

    /**
     * Sets merchant services.
     *
     * @param merchantServices the merchant services
     */
    public void setMerchantServices(List<MerchantServicesDTO> merchantServices) {
        this.merchantServices = merchantServices;
    }

    public List<PaymentsPayloadSettingsDTO> getPaymentSettings() {
        return paymentSettings;
    }

    public void setPaymentSettings(List<PaymentsPayloadSettingsDTO> paymentSettings) {
        this.paymentSettings = paymentSettings;
    }

    public List<UserPracticeDTO> getUserPractices() {
        return userPractices;
    }

    public void setUserPractices(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
    }

    public IntegratedPaymentPostModel getPaymentPostModel() {
        return paymentPostModel;
    }

    public void setPaymentPostModel(IntegratedPaymentPostModel paymentPostModel) {
        this.paymentPostModel = paymentPostModel;
    }

    public List<SimpleChargeItem> getSimpleChargeItems() {
        return simpleChargeItems;
    }

    public void setSimpleChargeItems(List<SimpleChargeItem> simpleChargeItems) {
        this.simpleChargeItems = simpleChargeItems;
    }

    public PaymentsTransactionHistory getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(PaymentsTransactionHistory transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public PaymentHistoryItemPayload getPatientRefund() {
        return patientRefund;
    }

    public void setPatientRefund(PaymentHistoryItemPayload patientRefund) {
        this.patientRefund = patientRefund;
    }

    public String getPaymentProfileId() {
        return paymentProfileId;
    }

    public void setPaymentProfileId(String paymentProfileId) {
        this.paymentProfileId = paymentProfileId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }

    /**
     * Make sure the current practice can accept payments
     * @return true if practice has merchant service account enabled
     */
    public boolean canMakePayments(String practiceId){
        for (UserPracticeDTO userPracticeDTO : getUserPractices()){
            if(userPracticeDTO.getPracticeId() != null && userPracticeDTO.getPracticeId().equals(practiceId)){
                return userPracticeDTO.isPayeezyEnabled();
            }
        }
        return false;
    }
}
