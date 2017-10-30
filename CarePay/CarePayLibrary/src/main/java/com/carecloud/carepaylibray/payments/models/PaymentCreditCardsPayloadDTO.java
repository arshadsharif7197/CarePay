package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.TokenizationService;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentCreditCardsPayloadDTO {

    @SerializedName("card_type")
    @Expose
    private String cardType;
    @SerializedName("card_number")
    @Expose
    private String cardNumber;
    @SerializedName("name_on_card")
    @Expose
    private String nameOnCard;
    @SerializedName("expire_dt")
    @Expose
    private String expireDt;
    @SerializedName("cvv")
    @Expose
    private String cvv;
    @SerializedName("card_id")
    @Expose
    private String creditCardsId;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("is_default")
    @Expose
    private boolean isDefault;
    @SerializedName("billing_information")
    @Expose
    private CreditCardBillingInformationDTO billingInformation = new CreditCardBillingInformationDTO();
    @SerializedName("tokenization_service")
    @Expose
    private TokenizationService tokenizationService;

    /**
     * @return The cardType
     */
    public String getCardType() {
        return cardType;
    }

    /**
     * @param cardType The card_type
     */
    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    /**
     * @return The cardNumber
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * @param cardNumber The card_number
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * @return The nameOnCard
     */
    public String getNameOnCard() {
        return nameOnCard;
    }

    /**
     * @param nameOnCard The name_on_card
     */
    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    /**
     * @return The expireDt
     */
    public String getExpireDt() {
        return expireDt;
    }

    /**
     * @param expireDt The expire_dt
     */
    public void setExpireDt(String expireDt) {
        this.expireDt = expireDt;
    }

    /**
     * @return The cvv
     */
    public String getCvv() {
        return cvv;
    }

    /**
     * @param cvv The cvv
     */
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    /**
     * @return The token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token The token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return The billingInformation
     */
    public CreditCardBillingInformationDTO getBillingInformation() {
        return billingInformation;
    }

    /**
     * @param billingInformation The billing_information
     */
    public void setBillingInformation(CreditCardBillingInformationDTO billingInformation) {
        this.billingInformation = billingInformation;
    }

    /**
     * @return creditCardsId
     */
    public String getCreditCardsId() {
        return creditCardsId;
    }

    /**
     * @param creditCardsId creditCardsId
     */
    public void setCreditCardsId(String creditCardsId) {
        this.creditCardsId = creditCardsId;
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
