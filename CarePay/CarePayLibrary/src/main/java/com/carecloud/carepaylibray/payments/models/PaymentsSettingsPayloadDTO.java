package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16
 */

public class PaymentsSettingsPayloadDTO {

    @SerializedName("payment_plans")
    @Expose
    private PaymentsSettingsPaymentPlansDTO paymentPlans = new PaymentsSettingsPaymentPlansDTO();

    @SerializedName("regular_payments")
    @Expose
    private PaymentsSettingsRegularPaymentsDTO regularPayments = new PaymentsSettingsRegularPaymentsDTO();


    /**
     * @return The paymentPlans
     */
    public PaymentsSettingsPaymentPlansDTO getPaymentPlans() {
        return paymentPlans;
    }

    /**
     * @param paymentPlans The payment_plans
     */
    public void setPaymentPlans(PaymentsSettingsPaymentPlansDTO paymentPlans) {
        this.paymentPlans = paymentPlans;
    }

    /**
     * Gets regular payments.
     *
     * @return the regular payments
     */
    public PaymentsSettingsRegularPaymentsDTO getRegularPayments() {
        return regularPayments;
    }

    /**
     * Sets regular payments.
     *
     * @param regularPayments the regular payments
     */
    public void setRegularPayments(PaymentsSettingsRegularPaymentsDTO regularPayments) {
        this.regularPayments = regularPayments;
    }
}
