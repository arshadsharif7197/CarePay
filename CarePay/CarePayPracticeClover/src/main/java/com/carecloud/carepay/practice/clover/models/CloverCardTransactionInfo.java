package com.carecloud.carepay.practice.clover.models;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/20/17.
 */

public class CloverCardTransactionInfo {

    @SerializedName("cardholderName")
    @Expose
    private String cardholderName;
    @SerializedName("authCode")
    @Expose
    private String authCode;
    @SerializedName("entryType")
    @Expose
    private String entryType;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("referenceId")
    @Expose
    private String referenceId;
    @SerializedName("last4")
    @Expose
    private String last4;
    @SerializedName("transactionNo")
    @Expose
    private String transactionNo;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("cardType")
    @Expose
    private String cardType;
    @SerializedName("vaultedCard")
    @Expose
    private CloverVaultedCard vaultedCard = new CloverVaultedCard();

    public String getCardholderName() {
        return cardholderName;
    }

    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getToken() {
        return !StringUtil.isNullOrEmpty(token)?token:"NO-TOKEN";
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getLast4() {
        return last4;
    }

    public void setLast4(String last4) {
        this.last4 = last4;
    }

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public CloverVaultedCard getVaultedCard() {
        return vaultedCard;
    }

    public void setVaultedCard(CloverVaultedCard vaultedCard) {
        this.vaultedCard = vaultedCard;
    }


}
