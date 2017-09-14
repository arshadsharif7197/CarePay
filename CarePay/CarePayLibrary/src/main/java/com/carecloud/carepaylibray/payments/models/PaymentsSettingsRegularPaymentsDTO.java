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
    private String partialPaymentsThreshold;
    @SerializedName("minimum_partial_payment_amount")
    @Expose
    private String minimumPartialPaymentAmount;

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

    /**
     * Get partial payment threshold as a double
     * @return partial payment threshold or 0
     */
    public double getPartialPaymentsThreshold() {
        try {
            return Double.parseDouble(partialPaymentsThreshold);
        }catch (NumberFormatException nfe){
            return 0D;
        }
    }

    public void setPartialPaymentsThreshold(double partialPaymentsThreshold) {
        this.partialPaymentsThreshold = String.valueOf(partialPaymentsThreshold);
    }

    /**
     * Get minimum partial payment as a double
     * @return minimum partial payment or 0
     */
    public double getMinimumPartialPaymentAmount() {
        try {
            return Double.parseDouble(minimumPartialPaymentAmount);
        }catch (NumberFormatException nfe){
            return 0D;
        }
    }

    public void setMinimumPartialPaymentAmount(double minimumPartialPaymentAmount) {
        this.minimumPartialPaymentAmount = String.valueOf(minimumPartialPaymentAmount);
    }
}
