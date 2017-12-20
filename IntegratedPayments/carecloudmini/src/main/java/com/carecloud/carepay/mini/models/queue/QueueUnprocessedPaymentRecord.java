package com.carecloud.carepay.mini.models.queue;

/**
 * Created by lmenendez on 12/18/17
 */

public class QueueUnprocessedPaymentRecord extends QueuePaymentRecord {

    private String payload;

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
