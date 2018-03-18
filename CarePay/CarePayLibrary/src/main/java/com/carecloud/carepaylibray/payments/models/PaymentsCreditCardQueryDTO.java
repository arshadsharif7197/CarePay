
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.base.models.BaseTransitionsPropertyModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsCreditCardQueryDTO {

    @SerializedName("credit_card_id")
    @Expose
    private BaseTransitionsPropertyModel paymentsCreditCardId = new BaseTransitionsPropertyModel();

    /**
     * 
     * @return
     *     The paymentsCreditCardIdDTO
     */
    public BaseTransitionsPropertyModel getPaymentsCreditCardIdDTO() {
        return paymentsCreditCardId;
    }

    /**
     * 
     * @param paymentsCreditCardId
     *     The credit_card_id
     */
    public void setPaymentsCreditCardIdDTO(BaseTransitionsPropertyModel paymentsCreditCardId) {
        this.paymentsCreditCardId = paymentsCreditCardId;
    }

}
