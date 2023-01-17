package com.carecloud.shamrocksdk.payment.models;



import androidx.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Model Class for handling Payment Method in {@link PaymentRequest Payment Requests}
 */

public class PaymentMethod {
    public static final String PAYMENT_METHOD_CARD = "card";
    public static final String PAYMENT_METHOD_NEW_CARD = "new_card";
    public static final String PAYMENT_METHOD_ACCOUNT = "transactional_account";

    @StringDef({PAYMENT_METHOD_CARD, PAYMENT_METHOD_NEW_CARD, PAYMENT_METHOD_ACCOUNT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PaymentMethodType{}

    @SerializedName("id")
    private String id;

    @SerializedName("type")
    private @PaymentMethodType String type;

    @SerializedName("card_data")
    private CardData cardData;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @PaymentMethodType String getType() {
        return type;
    }

    public void setType(@PaymentMethodType String type) {
        this.type = type;
    }

    public CardData getCardData() {
        return cardData;
    }

    public void setCardData(CardData cardData) {
        this.cardData = cardData;
    }

}
