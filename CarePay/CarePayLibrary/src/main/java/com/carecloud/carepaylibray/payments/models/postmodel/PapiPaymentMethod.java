package com.carecloud.carepaylibray.payments.models.postmodel;

import androidx.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 3/18/17
 */

public class PapiPaymentMethod {
    public static final String PAYMENT_METHOD_CARD = "card";
    public static final String PAYMENT_METHOD_NEW_CARD = "new_card";
    public static final String PAYMENT_METHOD_ACCOUNT = "transactional_account";

    @StringDef({PAYMENT_METHOD_CARD, PAYMENT_METHOD_NEW_CARD, PAYMENT_METHOD_ACCOUNT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PaymentMethodType{}


    @SerializedName("type")
    private @PaymentMethodType String paymentMethodType;

    @SerializedName("id")
    private String papiPaymentID;

    @SerializedName("card_data")
    private IntegratedPaymentCardData cardData;

    public @PaymentMethodType String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(@PaymentMethodType String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public String getPapiPaymentID() {
        return papiPaymentID;
    }

    public void setPapiPaymentID(String papiPaymentID) {
        this.papiPaymentID = papiPaymentID;
    }

    /**
     * Validate the papi payment method
     * @return true if valid
     */
    public boolean isValid(){
        return paymentMethodType != null &&
                (getPapiPaymentID() != null ||
                        (cardData != null && paymentMethodType.equals(PAYMENT_METHOD_NEW_CARD)));
    }

    public IntegratedPaymentCardData getCardData() {
        return cardData;
    }

    public void setCardData(IntegratedPaymentCardData cardData) {
        this.cardData = cardData;
    }
}
