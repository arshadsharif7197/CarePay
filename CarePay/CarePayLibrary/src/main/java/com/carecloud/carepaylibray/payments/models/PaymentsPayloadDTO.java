package com.carecloud.carepaylibray.payments.models;


import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.base.models.UserAuthModel;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItemPayload;
import com.carecloud.carepaylibray.payments.models.history.PaymentsTransactionHistory;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Rahul on 11/30/16
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
    @SerializedName("payment_plans")
    @Expose
    private List<PaymentPlanDTO> patientPaymentPlans = new ArrayList<>();
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
    @SerializedName("payment_plan")
    @Expose
    private PaymentPlanDTO paymentPlanUpdate;
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
    @Expose
    private String organizationId;
    @SerializedName("auth")
    @Expose
    private UserAuthModel userAuthModel = new UserAuthModel();
    @SerializedName("patient_statements")
    private List<PatientStatementDTO> patientStatements = new ArrayList<>();
    @SerializedName("payment_plan_created")
    private boolean paymentPlanCreated = false;


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
    public List<PaymentPlanDTO> getPatientPaymentPlans() {
        return patientPaymentPlans;
    }

    /**
     * @param patientPaymentPlans The patient_payment_plans
     */
    public void setPatientPaymentPlans(List<PaymentPlanDTO> patientPaymentPlans) {
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

    public UserAuthModel getUserAuthModel() {
        return userAuthModel;
    }

    public void setUserAuthModel(UserAuthModel userAuthModel) {
        this.userAuthModel = userAuthModel;
    }

    public List<PatientStatementDTO> getPatientStatements() {
        return patientStatements;
    }

    public void setPatientStatements(List<PatientStatementDTO> patientStatements) {
        this.patientStatements = patientStatements;
    }

    /**
     * Make sure the current practice can accept payments
     *
     * @return true if practice has merchant service account enabled
     */
    public boolean canMakePayments(String practiceId) {
        for (UserPracticeDTO userPracticeDTO : getUserPractices()) {
            if (userPracticeDTO.getPracticeId() != null && userPracticeDTO.getPracticeId().equals(practiceId)) {
                return userPracticeDTO.isPayeezyEnabled();
            }
        }
        return false;
    }

    /**
     * get only valid plans
     *
     * @param practiceId - optional, if provided will first filter plans by practice
     * @return active plans
     */
    public List<PaymentPlanDTO> getValidPlans(String practiceId, double amountToAdd) {
        List<PaymentPlanDTO> baseList = practiceId != null ?
                getActivePlans(practiceId) : getPatientPaymentPlans();
        List<PaymentPlanDTO> outputList = new ArrayList<>();
        for (PaymentsPayloadSettingsDTO settingsDTO : getPaymentSettings()) {
            if ((practiceId != null) && practiceId.equals(settingsDTO.getMetadata().getPracticeId())
                    && settingsDTO.getPayload().getPaymentPlans().isAddBalanceToExisting()) {
                for (PaymentSettingsBalanceRangeRule balanceRangeRule : settingsDTO.getPayload().getPaymentPlans().getBalanceRangeRules()) {
                    double minAmount = balanceRangeRule.getMinBalance().getValue();
                    double maxAmount = balanceRangeRule.getMaxBalance().getValue();
                    for (PaymentPlanDTO paymentPlanDTO : baseList) {
                        double pendingAmount = SystemUtil.safeSubtract(paymentPlanDTO.getPayload().getAmount(), paymentPlanDTO.getPayload().getAmountPaid());
                        double sumAmount = SystemUtil.safeAdd(pendingAmount, amountToAdd);
                        if (sumAmount >= minAmount && sumAmount <= maxAmount) {
                            outputList.add(paymentPlanDTO);
                        }
                    }
                }
            }
        }
        return outputList;
    }

    /**
     * get only active plans
     *
     * @param practiceId - optional, if provided will first filter plans by practice
     * @return active plans
     */
    public List<PaymentPlanDTO> getActivePlans(String practiceId) {
        List<PaymentPlanDTO> baseList = practiceId != null ?
                getFilteredPlans(practiceId) : getPatientPaymentPlans();
        List<PaymentPlanDTO> outputList = new ArrayList<>();
        for (PaymentPlanDTO paymentPlanDTO : baseList) {
            if (paymentPlanDTO.getPayload().getPaymentPlanDetails().getPaymentPlanStatus()
                    .equals(PaymentPlanDetailsDTO.STATUS_PROCESSING)) {
                outputList.add(paymentPlanDTO);
            }
        }
        return outputList;
    }

    /**
     * get filtered list of plans for a single practice
     *
     * @param practiceId practice id
     * @return filtered list of plans for the specified practice
     */
    public List<PaymentPlanDTO> getFilteredPlans(String practiceId) {
        List<PaymentPlanDTO> filteredList = new ArrayList<>();
        for (PaymentPlanDTO paymentPlanDTO : getPatientPaymentPlans()) {
            if (paymentPlanDTO.getMetadata().getPracticeId() != null &&
                    paymentPlanDTO.getMetadata().getPracticeId().equals(practiceId)) {
                filteredList.add(paymentPlanDTO);
            }
        }
        return filteredList;
    }

    public PaymentPlanDTO getPaymentPlanUpdate() {
        return paymentPlanUpdate;
    }

    public void setPaymentPlanUpdate(PaymentPlanDTO paymentPlanUpdate) {
        this.paymentPlanUpdate = paymentPlanUpdate;
    }

    public boolean isPaymentPlanCreated() {
        return paymentPlanCreated;
    }

    public void setPaymentPlanCreated(boolean paymentPlanCreated) {
        this.paymentPlanCreated = paymentPlanCreated;
    }

    /**
     * Reduce the list of balance items to remove all items currently on a Payment Plan
     * @param selectedBalance selected balance to put on plan
     * @param updateAmount true if amount should be updated by subtracting plans amount
     * @return PendingBalanceDTO with reduced details
     */
    public PendingBalanceDTO reduceBalanceItems(PendingBalanceDTO selectedBalance, boolean updateAmount){
        List<PaymentPlanDTO> currentPaymentPlans = getActivePlans(selectedBalance.getMetadata().getPracticeId());
        Map<String, Double> paymentPlanItems = new HashMap<>();
        for(PaymentPlanDTO paymentPlanDTO : currentPaymentPlans){
            for(PaymentPlanLineItem lineItem : paymentPlanDTO.getPayload().getLineItems()){
                Double amount = SystemUtil.safeSubtract(lineItem.getAmount(), lineItem.getAmountPaid());
                if(paymentPlanItems.containsKey(lineItem.getTypeId())){//we may have the line item split on more than one plan potentially
                    amount = SystemUtil.safeAdd(paymentPlanItems.get(lineItem.getTypeId()), lineItem.getAmount());//sum both items
                }
                paymentPlanItems.put(lineItem.getTypeId(), amount);
            }
        }

        String balanceHolder = DtoHelper.getStringDTO(selectedBalance);
        PendingBalanceDTO copyPendingBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, balanceHolder);

        List<BalanceItemDTO> reducedBalances;
        for(PendingBalancePayloadDTO pendingBalancePayloadDTO : copyPendingBalance.getPayload()){
            reducedBalances = new ArrayList<>();
            for(BalanceItemDTO balanceItemDTO : pendingBalancePayloadDTO.getDetails()){
                if(paymentPlanItems.containsKey(balanceItemDTO.getId().toString())){
                    Double paymentPlanLineItemAmount = paymentPlanItems.get(balanceItemDTO.getId().toString());
                    if(paymentPlanLineItemAmount < balanceItemDTO.getBalance()){//reduce the balance item by the payment plan item amount
                        double originalBalanceItemBalance = balanceItemDTO.getBalance();
                        balanceItemDTO.setBalance(SystemUtil.safeSubtract(originalBalanceItemBalance, paymentPlanLineItemAmount));
                        reducedBalances.add(balanceItemDTO);
                    }//else the entire balance item will be dropped
                } else {
                    reducedBalances.add(balanceItemDTO); //since this item was not already on PP we need to leave it
                }
            }
            pendingBalancePayloadDTO.setDetails(reducedBalances);
            if(updateAmount){
                pendingBalancePayloadDTO.setAmount(SystemUtil.safeSubtract(
                        pendingBalancePayloadDTO.getAmount(),
                        pendingBalancePayloadDTO.getPaymentPlansAmount()));
            }
        }
        return copyPendingBalance;
    }

    /**
     * Check if balance must be added to existing plan
     * @param amount amount to add
     * @param selectedBalance selected balance
     * @return true if only option is to add to existing
     */
    public boolean mustAddToExisting(double amount, PendingBalanceDTO selectedBalance) {
        String practiceId = selectedBalance.getMetadata().getPracticeId();
        for (PaymentsPayloadSettingsDTO settingsDTO : getPaymentSettings()) {
            if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
                PaymentsSettingsPaymentPlansDTO paymentPlanSettings = settingsDTO.getPayload().getPaymentPlans();
                return (!hasApplicableRule(paymentPlanSettings, amount) ||
                        !paymentPlanSettings.isCanHaveMultiple()) &&
                        !getValidPlans(practiceId, amount).isEmpty() &&
                        paymentPlanSettings.isAddBalanceToExisting();
            }
        }
        return false;
    }

    private boolean hasApplicableRule(PaymentsSettingsPaymentPlansDTO paymentPlanSettings, double amount) {
        for (PaymentSettingsBalanceRangeRule balanceRangeRule : paymentPlanSettings.getBalanceRangeRules()) {
            double minAmount = balanceRangeRule.getMinBalance().getValue();
            double maxAmount = balanceRangeRule.getMaxBalance().getValue();
            if (amount >= minAmount && amount <= maxAmount) {
                return true;
            }
        }
        return false;
    }

}
