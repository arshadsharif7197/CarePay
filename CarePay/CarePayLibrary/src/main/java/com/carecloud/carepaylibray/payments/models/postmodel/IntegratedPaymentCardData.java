package com.carecloud.carepaylibray.payments.models.postmodel;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 8/29/17
 */

public class IntegratedPaymentCardData {

    public static final String TOKENIZATION_CLOVER = "clover";
    public static final String TOKENIZATION_PAYEEZY = "payeezy";
    public static final String TOKENIZATION_ANDROID = "android_pay";

    @StringDef({TOKENIZATION_CLOVER, TOKENIZATION_PAYEEZY, TOKENIZATION_ANDROID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TokenizationService{}

    @SerializedName("card_type")
    private String cardType;

    @SerializedName("cardholder_name")
    private String nameOnCard;

    @SerializedName("expiration")
    private String expiryDate;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("last_four")
    private String cardNumber;

    @SerializedName("save_card")
    private boolean saveCard = false;

    @SerializedName("token")
    private String token;

    @SerializedName("tokenization_service")
    private @TokenizationService String tokenizationService;

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        isDefault = isDefault;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public boolean isSaveCard() {
        return saveCard;
    }

    public void setSaveCard(boolean saveCard) {
        this.saveCard = saveCard;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public @TokenizationService String getTokenizationService() {
        return tokenizationService;
    }

    public void setTokenizationService(@TokenizationService String tokenizationService) {
        this.tokenizationService = tokenizationService;
    }

}
