package com.carecloud.shamrocksdk.payment.models;



import androidx.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Model Class for handling card data in {@link PaymentRequest Payment Requests}
 */

public class CardData {
    public static final String TOKENIZATION_CLOVER = "clover";
    public static final String TOKENIZATION_PAYEEZY = "payeezy";
    public static final String TOKENIZATION_ANDROID = "android_pay";

    @StringDef({TOKENIZATION_CLOVER, TOKENIZATION_PAYEEZY, TOKENIZATION_ANDROID})
    @Retention(RetentionPolicy.SOURCE)
    @interface Tokenization{}

    @SerializedName("tokenization_service")
    @Tokenization private String tokenizationService;

    @SerializedName("cardType")
    private String cardType;

    @SerializedName("last_four")
    private String cardNumber;

    @SerializedName("cardholder_name")
    private String cardholderName;

    @SerializedName("expiration")
    private String expiration;

    @SerializedName("token")
    private String token;

    @SerializedName("cvv")
    private String cvvCode;

    @SerializedName("is_default")
    private boolean isDefault = false;

    @SerializedName("save_card")
    private boolean saveCard = false;

    public String getTokenizationService() {
        return tokenizationService;
    }

    public void setTokenizationService(String tokenizationService) {
        this.tokenizationService = tokenizationService;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCvvCode() {
        return cvvCode;
    }

    public void setCvvCode(String cvvCode) {
        this.cvvCode = cvvCode;
    }

}
