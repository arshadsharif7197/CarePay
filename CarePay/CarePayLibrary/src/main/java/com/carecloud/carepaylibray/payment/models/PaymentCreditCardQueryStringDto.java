
package com.carecloud.carepaylibray.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentCreditCardQueryStringDto {

    @SerializedName("credit_card_id")
    @Expose
    private PaymentsCreditCardIdModel paymentsCreditCardIdModel;

    /**
     * 
     * @return
     *     The paymentsCreditCardIdModel
     */
    public PaymentsCreditCardIdModel getPaymentsCreditCardIdModel() {
        return paymentsCreditCardIdModel;
    }

    /**
     * 
     * @param paymentsCreditCardIdModel
     *     The credit_card_id
     */
    public void setPaymentsCreditCardIdModel(PaymentsCreditCardIdModel paymentsCreditCardIdModel) {
        this.paymentsCreditCardIdModel = paymentsCreditCardIdModel;
    }

}
