package com.carecloud.carepay.practice.clover.DTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/20/17.
 */

public class CloverPaymentDTO {

    @SerializedName("result")
    @Expose
    private String result;

    @SerializedName("offline")
    @Expose
    private boolean offline = false;

    @SerializedName("cardTransacton")
    @Expose
    private CloverCardTransactionInfo cloverCardTransactionInfo = new CloverCardTransactionInfo();

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public CloverCardTransactionInfo getCloverCardTransactionInfo() {
        return cloverCardTransactionInfo;
    }

    public void setCloverCardTransactionInfo(CloverCardTransactionInfo cloverCardTransactionInfo) {
        this.cloverCardTransactionInfo = cloverCardTransactionInfo;
    }
}
