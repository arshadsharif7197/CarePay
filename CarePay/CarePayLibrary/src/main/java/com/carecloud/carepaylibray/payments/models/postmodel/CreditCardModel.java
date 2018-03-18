package com.carecloud.carepaylibray.payments.models.postmodel;

import com.carecloud.carepaylibray.payments.models.CreditCardBillingInformationDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/21/17.
 */

public class CreditCardModel {

    @SerializedName("tokenization_service")
    private TokenizationService tokenizationService;

    @SerializedName("card_type")
    private String cardType;

    @SerializedName("save")
    private boolean saveCard = false;

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("name_on_card")
    private String nameOnCard;

    @SerializedName("expire_dt")
    private String expiryDate;

    @SerializedName("token")
    private String token;

    @SerializedName("cvv")
    private String cvv;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("billing_information")
    private CreditCardBillingInformationDTO billingInformation;


    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public boolean isSaveCard() {
        return saveCard;
    }

    public void setSaveCard(boolean saveCard) {
        this.saveCard = saveCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public CreditCardBillingInformationDTO getBillingInformation() {
        return billingInformation;
    }

    public void setBillingInformation(CreditCardBillingInformationDTO billingInformation) {
        this.billingInformation = billingInformation;
    }

    /**
     * Verivy validity of Credit Card
     * @return true if credit card is valid
     */
    public boolean isValidCreditCard(){
        return cardNumber != null &&
                cardType != null &&
                nameOnCard != null &&
                expiryDate != null &&
                token != null &&
                billingInformation != null;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public TokenizationService getTokenizationService() {
        return tokenizationService;
    }

    public void setTokenizationService(TokenizationService tokenizationService) {
        this.tokenizationService = tokenizationService;
    }
}
