package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PaymentsSettingsRegularPaymentsDTO {

    @SerializedName("payment_methods")
    @Expose
    private List<PaymentsMethodsDTO> paymentMethods = new ArrayList<>();
    @SerializedName("post_to_oldest_balance")
    @Expose
    private boolean postToOldestBalance;
    @SerializedName("allow_partial_payments")
    @Expose
    private boolean allowPartialPayments;
    @SerializedName("partial_payments_threshold")
    @Expose
    private double partialPaymentsThreshold;
    @SerializedName("minimum_partial_payment_amount")
    @Expose
    private double minimumPartialPaymentAmount;

    /**
     * Gets payment methods.
     *
     * @return the payment methods
     */
    public List<PaymentsMethodsDTO> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * Sets payment methods.
     *
     * @param paymentMethods the payment methods
     */
    public void setPaymentMethods(List<PaymentsMethodsDTO> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     * Is post to oldest balance boolean.
     *
     * @return the boolean
     */
    public boolean isPostToOldestBalance() {
        return postToOldestBalance;
    }

    /**
     * Sets post to oldest balance.
     *
     * @param postToOldestBalance the post to oldest balance
     */
    public void setPostToOldestBalance(boolean postToOldestBalance) {
        this.postToOldestBalance = postToOldestBalance;
    }

    /**
     * Is allow partial payments boolean.
     *
     * @return the boolean
     */
    public boolean isAllowPartialPayments() {
        return allowPartialPayments;
    }

    /**
     * Sets allow partial payments.
     *
     * @param allowPartialPayments the allow partial payments
     */
    public void setAllowPartialPayments(boolean allowPartialPayments) {
        this.allowPartialPayments = allowPartialPayments;
    }

    public double getPartialPaymentsThreshold() {
        return partialPaymentsThreshold;
    }

    public void setPartialPaymentsThreshold(double partialPaymentsThreshold) {
        this.partialPaymentsThreshold = partialPaymentsThreshold;
    }

    public double getMinimumPartialPaymentAmount() {
        return minimumPartialPaymentAmount;
    }

    public void setMinimumPartialPaymentAmount(double minimumPartialPaymentAmount) {
        this.minimumPartialPaymentAmount = minimumPartialPaymentAmount;
    }
}
