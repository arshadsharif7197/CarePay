package com.carecloud.carepay.mini.models.queue;

import com.orm.SugarRecord;

/**
 * Created by lmenendez on 8/24/17
 */

public class QueuePaymentRecord extends SugarRecord {

    private String paymentRequestId;

    public QueuePaymentRecord(){}

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }
}
