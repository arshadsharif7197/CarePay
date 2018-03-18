package com.carecloud.carepaylibray.demographicsettings.models;


import com.carecloud.carepaylibray.payments.models.CreditCardBillingInformationDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017
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
    @SerializedName("card_id")
    @Expose
    private String hashCreditCardsId;
    @SerializedName("authorized")
    @Expose
    private Boolean authorized;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("saved_on_file")
    @Expose
    private boolean savedOnFile;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_default")
    @Expose
    private boolean isDefault;
    @SerializedName("payment_profile_id")
    @Expose
    private String paymentProfileId;
    @SerializedName("billing_information")
    @Expose
    private CreditCardBillingInformationDTO billingInformation = new CreditCardBillingInformationDTO();

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

    public CreditCardBillingInformationDTO getBillingInformation() {
        return billingInformation;
    }

    public void setBillingInformation(CreditCardBillingInformationDTO billingInformation) {
        this.billingInformation = billingInformation;
    }

    public boolean isSavedOnFile() {
        return savedOnFile;
    }

    public void setSavedOnFile(boolean savedOnFile) {
        this.savedOnFile = savedOnFile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        isDefault = isDefault;
    }

    public String getPaymentProfileId() {
        return paymentProfileId;
    }

    public void setPaymentProfileId(String paymentProfileId) {
        this.paymentProfileId = paymentProfileId;
    }
}