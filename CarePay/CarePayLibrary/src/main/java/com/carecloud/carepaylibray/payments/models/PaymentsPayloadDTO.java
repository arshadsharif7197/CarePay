package com.carecloud.carepaylibray.payments.models;


import android.support.annotation.NonNull;

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
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.carecloud.carepaylibray.retail.models.RetailProductsModel;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
    @SerializedName("one_time_payment")
    private ScheduledPaymentModel scheduledPaymentModel = new ScheduledPaymentModel();
    @SerializedName("one_time_payments")
    private List<ScheduledPaymentModel> scheduledOneTimePayments = new ArrayList<>();
    @SerializedName("retail")
    private RetailProductsModel retailProducts = new RetailProductsModel();

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

    public ScheduledPaymentModel getScheduledPaymentModel() {
        return scheduledPaymentModel;
    }

    public void setScheduledPaymentModel(ScheduledPaymentModel scheduledPaymentModel) {
        this.scheduledPaymentModel = scheduledPaymentModel;
    }

    public List<ScheduledPaymentModel> getScheduledOneTimePayments() {
        return scheduledOneTimePayments;
    }

    public void setScheduledOneTimePayments(List<ScheduledPaymentModel> scheduledOneTimePayments) {
        this.scheduledOneTimePayments = scheduledOneTimePayments;
    }

    public RetailProductsModel getRetailProducts() {
        return retailProducts;
    }

    public void setRetailProducts(RetailProductsModel retailProducts) {
        this.retailProducts = retailProducts;
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
     * Verify is pratice has payment methods enabled
     * 
     * @param practiceId
     * @return true if practice has at least one payment method enabled
     */
    public boolean hasPaymentMethods(String practiceId) {
        PaymentsPayloadSettingsDTO settingsDTO = getPaymentSetting(practiceId);
        return settingsDTO.getPayload().getRegularPayments().getPaymentMethods().size() > 0;
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
        PaymentsSettingsPaymentPlansDTO settingsDTO = getPaymentSetting(practiceId)
                .getPayload().getPaymentPlans();
        if (settingsDTO.isAddBalanceToExisting()) {
            for (PaymentPlanDTO paymentPlanDTO : baseList) {
                double pendingAmount = SystemUtil.safeSubtract(paymentPlanDTO.getPayload()
                        .getAmount(), paymentPlanDTO.getPayload().getAmountPaid());
                double sumAmount = SystemUtil.safeAdd(pendingAmount, amountToAdd);
                for (PaymentSettingsBalanceRangeRule balanceRangeRule : settingsDTO.getBalanceRangeRules()) {
                    double minAmount = balanceRangeRule.getMinBalance().getValue();
                    double maxAmount = balanceRangeRule.getMaxBalance().getValue();
                    if (sumAmount >= minAmount && sumAmount <= maxAmount &&
                            planFrequencyMatchesRuleInterval(paymentPlanDTO, balanceRangeRule) &&
                            isFrequencyEnabled(settingsDTO, balanceRangeRule)) {
                        outputList.add(paymentPlanDTO);
                        break;
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
    public List<PaymentPlanDTO> getFilteredPlans(@NonNull String practiceId) {
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
     *
     * @param selectedBalance selected balance to put on plan
     * @param updateAmount    true if amount should be updated by subtracting plans amount
     * @return PendingBalanceDTO with reduced details
     */
    public PendingBalanceDTO reduceBalanceItems(PendingBalanceDTO selectedBalance, boolean updateAmount) {
        List<PaymentPlanDTO> currentPaymentPlans = getActivePlans(selectedBalance.getMetadata().getPracticeId());
        Map<String, Double> paymentPlanItems = new HashMap<>();
        for (PaymentPlanDTO paymentPlanDTO : currentPaymentPlans) {
            for (PaymentPlanLineItem lineItem : paymentPlanDTO.getPayload().getLineItems()) {
                Double amount = SystemUtil.safeSubtract(lineItem.getAmount(), lineItem.getAmountPaid());
                if (paymentPlanItems.containsKey(lineItem.getTypeId())) {
                    //we may have the line item split on more than one plan potentially

                    //sum both items
                    amount = SystemUtil.safeAdd(paymentPlanItems.get(lineItem.getTypeId()), lineItem.getAmount());
                }
                paymentPlanItems.put(lineItem.getTypeId(), amount);
            }
        }

        String balanceHolder = DtoHelper.getStringDTO(selectedBalance);
        PendingBalanceDTO copyPendingBalance = DtoHelper.getConvertedDTO(PendingBalanceDTO.class, balanceHolder);

        List<BalanceItemDTO> reducedBalances;
        for (PendingBalancePayloadDTO pendingBalancePayloadDTO : copyPendingBalance.getPayload()) {
            reducedBalances = new ArrayList<>();
            for (BalanceItemDTO balanceItemDTO : pendingBalancePayloadDTO.getDetails()) {
                if (paymentPlanItems.containsKey(balanceItemDTO.getId().toString())) {
                    Double paymentPlanLineItemAmount = paymentPlanItems.get(balanceItemDTO.getId().toString());
                    if (paymentPlanLineItemAmount < balanceItemDTO.getBalance()) {
                        //reduce the balance item by the payment plan item amount
                        double originalBalanceItemBalance = balanceItemDTO.getBalance();
                        balanceItemDTO.setBalance(SystemUtil
                                .safeSubtract(originalBalanceItemBalance, paymentPlanLineItemAmount));
                        reducedBalances.add(balanceItemDTO);
                    }//else the entire balance item will be dropped
                } else {
                    //since this item was not already on PP we need to leave it
                    reducedBalances.add(balanceItemDTO);
                }
            }
            pendingBalancePayloadDTO.setDetails(reducedBalances);
            if (updateAmount) {
                pendingBalancePayloadDTO.setAmount(SystemUtil.safeSubtract(
                        pendingBalancePayloadDTO.getAmount(),
                        pendingBalancePayloadDTO.getPaymentPlansAmount()));
            }
        }
        return copyPendingBalance;
    }

    /**
     * Check if balance must be added to existing plan
     *
     * @param amount          amount to add
     * @param selectedBalance selected balance
     * @return true if only option is to add to existing
     */
    public boolean mustAddToExisting(double amount, PendingBalanceDTO selectedBalance) {
        String practiceId = selectedBalance.getMetadata().getPracticeId();
        PaymentsPayloadSettingsDTO settingsDTO = getPaymentSetting(practiceId);
        if (practiceId != null && practiceId.equals(settingsDTO.getMetadata().getPracticeId())) {
            PaymentsSettingsPaymentPlansDTO paymentPlanSettings = settingsDTO.getPayload().getPaymentPlans();
            return (!hasApplicableRule(paymentPlanSettings, amount) ||
                    !paymentPlanSettings.isCanHaveMultiple()) &&
                    !getValidPlans(practiceId, amount).isEmpty() &&
                    paymentPlanSettings.isAddBalanceToExisting();
        }

        return false;
    }

    public boolean hasApplicableRule(PaymentsSettingsPaymentPlansDTO paymentPlanSettings, double amount, @PaymentPlanModel.FrequencyDef String... frequencies) {
        for (PaymentSettingsBalanceRangeRule balanceRangeRule : paymentPlanSettings.getBalanceRangeRules()) {
            if (isFrequencyEnabled(paymentPlanSettings, balanceRangeRule) && (frequencies == null || frequencies.length == 0 ||
                    SystemUtil.arrayContains(frequencies, balanceRangeRule.getMaxDuration().getInterval()))) {
                double minAmount = balanceRangeRule.getMinBalance().getValue();
                double maxAmount = balanceRangeRule.getMaxBalance().getValue();
                if (amount >= minAmount && amount <= maxAmount) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isFrequencyEnabled(PaymentsSettingsPaymentPlansDTO settingsDTO,
                                       PaymentSettingsBalanceRangeRule balanceRangeRule) {
        if (balanceRangeRule.getMaxDuration().getInterval().equals(PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS)) {
            return settingsDTO.getFrequencyCode().getMonthly().isAllowed();
        } else {
            return settingsDTO.getFrequencyCode().getWeekly().isAllowed();
        }
    }

    /**
     * Calculate the minimum amount that can be placed on a plan
     *
     * @param practiceId practice id for the plan
     * @return minimum amount that can be placed on a new plan or added to an existing plan
     */
    public double getMinimumAllowablePlanAmount(@NonNull String practiceId) {
        PaymentsSettingsPaymentPlansDTO settingsDTO = getPaymentSetting(practiceId).getPayload()
                .getPaymentPlans();
        double minPaymentPlanAmount = Double.MAX_VALUE;
        for (PaymentSettingsBalanceRangeRule rangeRule : settingsDTO.getBalanceRangeRules()) {
            double minBalance = rangeRule.getMinBalance().getValue();
            if (minBalance < minPaymentPlanAmount && isFrequencyEnabled(settingsDTO, rangeRule)) {
                minPaymentPlanAmount = minBalance;
            }
        }

        //checkif minimum amount can be added to an existing
        List<PaymentPlanDTO> activePlans = getActivePlans(practiceId);
        if (!activePlans.isEmpty()) {
            if (getValidPlans(practiceId, 0.01).isEmpty()) {
                double maxPlanAmount = 0D;
                for (PaymentPlanDTO paymentPlan : activePlans) {
                    double pendingAmount = paymentPlan.getPayload().getAmount();
                    if (pendingAmount > maxPlanAmount) {
                        maxPlanAmount = pendingAmount;
                    }
                }
                if (minPaymentPlanAmount > maxPlanAmount) {
                    return SystemUtil.safeSubtract(minPaymentPlanAmount, maxPlanAmount);
                } else {
                    return minPaymentPlanAmount;
                }
            } else {
                return 0D;
            }
        } else {
            return minPaymentPlanAmount;
        }

    }

    /**
     * Calculate the maximum amount that can be placed on a payment plan
     *
     * @param practiceId practice id for the plan
     * @return maximum amount that can be placed on a new plan or added to an existing plan if a new one cannot be created
     */
    public double getMaximumAllowablePlanAmount(@NonNull String practiceId) {
        PaymentsSettingsPaymentPlansDTO settingsDTO = getPaymentSetting(practiceId).getPayload()
                .getPaymentPlans();
        double maxPaymentPlanAmount = 0D;
        for (PaymentSettingsBalanceRangeRule rangeRule : settingsDTO.getBalanceRangeRules()) {
            double maxBalance = rangeRule.getMaxBalance().getValue();
            if (maxBalance > maxPaymentPlanAmount && isFrequencyEnabled(settingsDTO, rangeRule)) {
                maxPaymentPlanAmount = maxBalance;
            }
        }

        //checkif max amount can be added to a new plan
        List<PaymentPlanDTO> activePlans = getActivePlans(practiceId);
        if (activePlans.isEmpty() || settingsDTO.isCanHaveMultiple()) {
            return maxPaymentPlanAmount;
        } else if (settingsDTO.isAddBalanceToExisting()) {
            double minPlanAmount = Double.MAX_VALUE;
            for (PaymentPlanDTO paymentPlan : activePlans) {
                double pendingAmount = paymentPlan.getPayload().getAmount();
                if (pendingAmount < minPlanAmount) {
                    minPlanAmount = pendingAmount;
                }
            }
            if (minPlanAmount > maxPaymentPlanAmount) {
                return 0D;
            }
            return SystemUtil.safeSubtract(maxPaymentPlanAmount, minPlanAmount);
        }


        return 0D;
    }

    /**
     * Find a scheduled payment for a specific plan that is scheduled for a future date
     *
     * @param paymentPlanDTO payment plan
     * @return future scheduled payment or null if none
     */
    public ScheduledPaymentModel findScheduledPayment(PaymentPlanDTO paymentPlanDTO) {
        String planId = paymentPlanDTO.getMetadata().getPaymentPlanId();
        for (ScheduledPaymentModel scheduledPaymentModel : getScheduledOneTimePayments()) {
            if (scheduledPaymentModel.getMetadata().getPaymentPlanId().equals(planId)) {
                Date scheduledDate = DateUtil.getInstance()
                        .setDateRaw(scheduledPaymentModel.getPayload().getPaymentDate()).getDate();
                if (scheduledDate.after(new Date())) {
                    return scheduledPaymentModel;
                }
            }
        }
        return null;
    }

    public PaymentsPayloadSettingsDTO getPaymentSetting(@NonNull String practiceId) {
        for (PaymentsPayloadSettingsDTO paymentSettings : getPaymentSettings()) {
            if (paymentSettings.getMetadata().getPracticeId().equals(practiceId)) {
                return paymentSettings;
            }
        }
        return null;
    }

    public boolean planFrequencyMatchesRuleInterval(PaymentPlanDTO paymentPlanDTO,
                                                    PaymentSettingsBalanceRangeRule balanceRangeRule) {
        switch (paymentPlanDTO.getPayload().getPaymentPlanDetails().getFrequencyCode()) {
            case PaymentPlanModel.FREQUENCY_MONTHLY:
                return balanceRangeRule.getMaxDuration().getInterval()
                        .equals(PaymentSettingsBalanceRangeRule.INTERVAL_MONTHS);
            case PaymentPlanModel.FREQUENCY_WEEKLY:
                return balanceRangeRule.getMaxDuration().getInterval()
                        .equals(PaymentSettingsBalanceRangeRule.INTERVAL_WEEKS);
            default:
                return false;
        }
    }

    public UserPracticeDTO getUserPractice(String practiceId) {
        for(UserPracticeDTO userPracticeDTO : userPractices){
            if(userPracticeDTO.getPracticeId().equals(practiceId)){
                return userPracticeDTO;
            }
        }
        return null;
    }

    public boolean isPrepayment() {
        try {
            return getPaymentPostModel().getMetadata().getAppointmentRequestDTO() != null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
