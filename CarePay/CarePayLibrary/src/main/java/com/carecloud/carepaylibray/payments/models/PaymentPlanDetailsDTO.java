package com.carecloud.carepaylibray.payments.models;

/**
 * Created by Rahul on 11/30/16
 */

import android.support.annotation.StringDef;

import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class PaymentPlanDetailsDTO extends PaymentPlanModel {
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_DISABLED = "paused";
    public static final String STATUS_CANCELLED = "cancelled";
    public static final String STATUS_COMPLETED = "completed";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({STATUS_PROCESSING, STATUS_DISABLED, STATUS_CANCELLED, STATUS_COMPLETED})
    public @interface PlanStatus {
    }

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("status")
    @Expose
    @PlanStatus
    private String paymentPlanStatus;

    @SerializedName("history")
    @Expose
    private List<PaymentPlanHistory> paymentPlanHistoryList = new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @PlanStatus
    public String getPaymentPlanStatus() {
        return paymentPlanStatus;
    }

    public void setPaymentPlanStatus(@PlanStatus String paymentPlanStatus) {
        this.paymentPlanStatus = paymentPlanStatus;
    }

    public List<PaymentPlanHistory> getPaymentPlanHistoryList() {
        return paymentPlanHistoryList;
    }

    public void setPaymentPlanHistoryList(List<PaymentPlanHistory> paymentPlanHistoryList) {
        this.paymentPlanHistoryList = paymentPlanHistoryList;
    }

    /**
     * Get all payment plan history items that are not one time payments
     *
     * @return filtered list
     */
    public List<PaymentPlanHistory> getFilteredHistory() {
        List<PaymentPlanHistory> filteredHistory = new ArrayList<>();
        for (PaymentPlanHistory paymentPlanHistory : getPaymentPlanHistoryList()) {
            if (!paymentPlanHistory.isOneTimePayment() && paymentPlanHistory.getAmount() > 0) {
                filteredHistory.add(paymentPlanHistory);
            }
        }
        return filteredHistory;
    }

    /**
     * Get all payment plan history items that are one time payments
     *
     * @return filtered list
     */
    public List<PaymentPlanHistory> getOneTimePayments() {
        List<PaymentPlanHistory> oneTimePayments = new ArrayList<>();
        for (PaymentPlanHistory paymentPlanHistory : getPaymentPlanHistoryList()) {
            if (paymentPlanHistory.isOneTimePayment() && paymentPlanHistory.getAmount() > 0) {
                oneTimePayments.add(paymentPlanHistory);
            }
        }
        return oneTimePayments;
    }

    /**
     * Get all payment plan history items that successful
     *
     * @return filtered list
     */
    public List<PaymentPlanHistory> getSuccessfulPaymentHistory() {
        List<PaymentPlanHistory> paymentHistory = new ArrayList<>();
        for (PaymentPlanHistory paymentPlanHistory : getPaymentPlanHistoryList()) {
            if (paymentPlanHistory.getAmount() > 0) {
                paymentHistory.add(paymentPlanHistory);
            }
        }
        return paymentHistory;
    }

}
