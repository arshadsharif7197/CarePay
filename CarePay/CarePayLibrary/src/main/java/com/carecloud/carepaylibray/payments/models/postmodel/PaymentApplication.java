package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/18/17.
 */

public class PaymentApplication {

    @SerializedName("debit_transaction_id")
    private long debitTransactionID;

    public long getDebitTransactionID() {
        return debitTransactionID;
    }

    public void setDebitTransactionID(long debitTransactionID) {
        this.debitTransactionID = debitTransactionID;
    }
}
