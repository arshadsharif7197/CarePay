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

    @SerializedName("payment_methods")
    @Expose
    private List<PaymentsMethodsDTO> paymentMethods = new ArrayList<>();

    @SerializedName("balance_range_rules")
    @Expose
    private List<PaymentSettingsBalanceRangeRule> balanceRangeRules = new ArrayList<>();

    @SerializedName("add_balance_to_new_plan")
    @Expose
    private boolean addBalanceToNew = true;

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

    public boolean isAddBalanceToNew() {
        return addBalanceToNew;
    }

    public void setAddBalanceToNew(boolean addBalanceToNew) {
        this.addBalanceToNew = addBalanceToNew;
    }
}