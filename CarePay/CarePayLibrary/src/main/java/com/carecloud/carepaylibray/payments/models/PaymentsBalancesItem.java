package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 1/05/17.
 */

public class PaymentsBalancesItem extends PaymentListItem {

    @SerializedName("metadata")
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();

    @SerializedName("balance")
    private PendingBalancePayloadDTO balance;

    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public PendingBalancePayloadDTO getBalance() {
        return balance;
    }

    public void setBalance(PendingBalancePayloadDTO balance) {
        this.balance = balance;
    }
}
