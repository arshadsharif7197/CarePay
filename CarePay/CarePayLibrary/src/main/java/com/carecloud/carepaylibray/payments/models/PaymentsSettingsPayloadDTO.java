package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsSettingsPayloadDTO {

    @SerializedName("payment_methods")
    @Expose
    private List<PaymentsMethodsDTO> paymentMethods = new ArrayList<>();
    @SerializedName("payment_plans")
    @Expose
    private PaymentsSettingsPayloadPlansDTO paymentPlans = new PaymentsSettingsPayloadPlansDTO();
    @SerializedName("credit_card_type")
    @Expose
    private List<PaymentsSettingsPayloadCreditCardTypesDTO> creditCardType = new ArrayList<>();
    @SerializedName("regular_payments")
    @Expose
    private PaymentsSettingsRegularPaymentsDTO regularPayments = new PaymentsSettingsRegularPaymentsDTO();

    /**
     * @return The paymentMethods
     */
    public List<PaymentsMethodsDTO> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     * @param paymentMethods The payment_methods
     */
    public void setPaymentMethods(List<PaymentsMethodsDTO> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    /**
     * @return The paymentPlans
     */
    public PaymentsSettingsPayloadPlansDTO getPaymentPlans() {
        return paymentPlans;
    }

    /**
     * @param paymentPlans The payment_plans
     */
    public void setPaymentPlans(PaymentsSettingsPayloadPlansDTO paymentPlans) {
        this.paymentPlans = paymentPlans;
    }

    /**
     * @return The creditCardType
     */
    public List<PaymentsSettingsPayloadCreditCardTypesDTO> getCreditCardType() {
        return creditCardType;
    }

    /**
     * @param creditCardType The credit_card_type
     */
    public void setCreditCardType(List<PaymentsSettingsPayloadCreditCardTypesDTO> creditCardType) {
        this.creditCardType = creditCardType;
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
