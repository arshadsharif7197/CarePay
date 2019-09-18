package com.carecloud.carepaylibray.payeeze.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2019-06-21.
 */
public class TokenizeBody {

    private String type;
    private String auth;
    @SerializedName("ta_token")
    private String taToken = "NOIW";
    @SerializedName("credit_card")
    private CreditCard creditCard;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getTaToken() {
        return taToken;
    }

    public void setTaToken(String taToken) {
        this.taToken = taToken;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
