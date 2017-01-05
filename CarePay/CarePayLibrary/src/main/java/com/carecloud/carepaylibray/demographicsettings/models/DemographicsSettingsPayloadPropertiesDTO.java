package com.carecloud.carepaylibray.demographicsettings.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsPayloadPropertiesDTO {

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
    @SerializedName("hash_credit_cards_id")
    @Expose
    private String hashCreditCardsId;
    @SerializedName("authorized")
    @Expose
    private Boolean authorized;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("billing_information")
    @Expose
    private DemographicsSettingsBillingInformationDTO billingInformation;

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

    public String getNameOnCard() {
        return nameOnCard;
    }

    public void setNameOnCard(String nameOnCard) {
        this.nameOnCard = nameOnCard;
    }

    public String getExpireDt() {
        return expireDt;
    }

    public void setExpireDt(String expireDt) {
        this.expireDt = expireDt;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getHashCreditCardsId() {
        return hashCreditCardsId;
    }

    public void setHashCreditCardsId(String hashCreditCardsId) {
        this.hashCreditCardsId = hashCreditCardsId;
    }

    public Boolean getAuthorized() {
        return authorized;
    }

    public void setAuthorized(Boolean authorized) {
        this.authorized = authorized;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DemographicsSettingsBillingInformationDTO getBillingInformation() {
        return billingInformation;
    }

    public void setBillingInformation(DemographicsSettingsBillingInformationDTO billingInformation) {
        this.billingInformation = billingInformation;
    }

}

