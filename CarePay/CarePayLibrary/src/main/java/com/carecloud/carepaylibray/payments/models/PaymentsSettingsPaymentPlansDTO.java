package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/30/16
 */

public class PaymentsSettingsPaymentPlansDTO {

    @SerializedName("enabled")
    @Expose
    private boolean paymentPlansEnabled = false;

    @SerializedName("request_new_plan")
    @Expose
    private boolean requestNewPlan = false;

    @SerializedName("can_create_multiple_plans")
    @Expose
    private boolean canHaveMultiple = false;

    @SerializedName("add_balance_to_existing")
    @Expose
    private boolean addBalanceToExisting = false;

    @SerializedName("can_cancel_plan")
    @Expose
    private boolean canCancelPlan = false;

    @SerializedName("payment_methods")
    @Expose
    private List<PaymentsMethodsDTO> paymentMethods = new ArrayList<>();

    @SerializedName("balance_range_rules")
    @Expose
    private List<PaymentSettingsBalanceRangeRule> balanceRangeRules = new ArrayList<>();

    @SerializedName("terms_and_conditions")
    @Expose
    private TermsAndConditionsDTO termsAndConditions = new TermsAndConditionsDTO();

    @SerializedName("frequency_code")
    @Expose
    private PaymentSettingFrequencyCode frequencyCode = new PaymentSettingFrequencyCode();

    public boolean isPaymentPlansEnabled() {
        return paymentPlansEnabled;
    }

    public void setPaymentPlansEnabled(boolean paymentPlansEnabled) {
        this.paymentPlansEnabled = paymentPlansEnabled;
    }

    public List<PaymentsMethodsDTO> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<PaymentsMethodsDTO> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public List<PaymentSettingsBalanceRangeRule> getBalanceRangeRules() {
        return balanceRangeRules;
    }

    public void setBalanceRangeRules(List<PaymentSettingsBalanceRangeRule> balanceRangeRules) {
        this.balanceRangeRules = balanceRangeRules;
    }

    public boolean isAddBalanceToExisting() {
        return addBalanceToExisting;
    }

    public void setAddBalanceToExisting(boolean addBalanceToExisting) {
        this.addBalanceToExisting = addBalanceToExisting;
    }

    public boolean isCanHaveMultiple() {
        return canHaveMultiple;
    }

    public void setCanHaveMultiple(boolean canHaveMultiple) {
        this.canHaveMultiple = canHaveMultiple;
    }

    public boolean isRequestNewPlan() {
        return requestNewPlan;
    }

    public void setRequestNewPlan(boolean requestNewPlan) {
        this.requestNewPlan = requestNewPlan;
    }

    public TermsAndConditionsDTO getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(TermsAndConditionsDTO termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public PaymentSettingFrequencyCode getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(PaymentSettingFrequencyCode frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public boolean isCanCancelPlan() {
        return canCancelPlan;
    }

    public void setCanCancelPlan(boolean canCancelPlan) {
        this.canCancelPlan = canCancelPlan;
    }
}
