package com.carecloud.carepaylibray.payeeze.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-21.
 */
public class CreditCard {

    @SerializedName("type")
    private String type;
    @SerializedName("cardholder_name")
    private String cardHolderName;
    @SerializedName("card_number")
    private String cardNumber;
    @SerializedName("exp_date")
    private String expDate;
    @SerializedName("cvv")
    private String cvv;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
