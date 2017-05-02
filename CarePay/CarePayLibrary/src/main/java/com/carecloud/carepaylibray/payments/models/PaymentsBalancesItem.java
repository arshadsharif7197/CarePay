package com.carecloud.carepaylibray.payments.models;

/**
 * @author pjohnson on 1/05/17.
 */

public class PaymentsBalancesItem {
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();
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
