package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by lmenendez on 2/21/17.
 */

public class TransactionResponse {

    @SerializedName("transaction_id")
    private  String transactionID;

    @SerializedName("raw_respose")
    private JSONObject response;

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    /**
     * Verify validity of transaction response object
     * @return true if valid
     */
    public boolean isValid(){
        return transactionID != null;
    }
}
