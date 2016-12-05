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
    //private List<PaymentSettingsPayMethodsDTO> paymentMethods = new ArrayList<PaymentSettingsPayMethodsDTO>();
    private List<PaymentMethodDTO> paymentMethods = new ArrayList<PaymentMethodDTO>();
    @SerializedName("payment_plans")
    @Expose
    private PaymentsSettingsPayloadPlansDTO paymentPlans;
    @SerializedName("credit_card_type")
    @Expose
    private List<PaymentsSettingsPayloadCreditCardTypesDTO> creditCardType = new ArrayList<PaymentsSettingsPayloadCreditCardTypesDTO>();

    /**
     *
     * @return
     * The paymentMethods
     */
    //public List<PaymentSettingsPayMethodsDTO> getPaymentMethods() {
      //  return paymentMethods;
    //}
    public List<PaymentMethodDTO> getPaymentMethods() {
        return paymentMethods;
    }

    /**
     *
     * @param paymentMethods
     * The payment_methods
     */
    //public void setPaymentMethods(List<PaymentSettingsPayMethodsDTO> paymentMethods) {
    //    this.paymentMethods = paymentMethods;
    //}
    public void setPaymentMethods(List<PaymentMethodDTO> paymentMethods) {
            this.paymentMethods = paymentMethods;
        }

    /**
     *
     * @return
     * The paymentPlans
     */
    public PaymentsSettingsPayloadPlansDTO getPaymentPlans() {
        return paymentPlans;
    }

    /**
     *
     * @param paymentPlans
     * The payment_plans
     */
    public void setPaymentPlans(PaymentsSettingsPayloadPlansDTO paymentPlans) {
        this.paymentPlans = paymentPlans;
    }

    /**
     *
     * @return
     * The creditCardType
     */
    public List<PaymentsSettingsPayloadCreditCardTypesDTO> getCreditCardType() {
        return creditCardType;
    }

    /**
     *
     * @param creditCardType
     * The credit_card_type
     */
    public void setCreditCardType(List<PaymentsSettingsPayloadCreditCardTypesDTO> creditCardType) {
        this.creditCardType = creditCardType;
    }

}
