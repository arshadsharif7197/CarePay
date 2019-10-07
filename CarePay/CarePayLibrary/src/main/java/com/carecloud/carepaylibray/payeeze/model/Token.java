package com.carecloud.carepaylibray.payeeze.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-21.
 */
public class Token {

    @SerializedName("type")
    private String type;
    @SerializedName("cardholder_name")
    private String cardHolderName;
    @SerializedName("exp_date")
    private String expDate;
    @SerializedName("value")
    private String value;

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

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
