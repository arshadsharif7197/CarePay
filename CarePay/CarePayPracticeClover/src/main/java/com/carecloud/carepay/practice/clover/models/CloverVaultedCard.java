package com.carecloud.carepay.practice.clover.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/21/17.
 */

public class CloverVaultedCard {

    @SerializedName("cardholderName")
    @Expose
    private String cardholderName;
    @SerializedName("first6")
    @Expose
    private String first6;
    @SerializedName("expirationDate")
    @Expose
    private String expirationDate;
    @SerializedName("last4")
    @Expose
    private String last4;
    @SerializedName("token")
    @Expose
    private String token;

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getFirst6() {
        return first6;
    }

    public void setFirst6(String first6) {
        this.first6 = first6;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
