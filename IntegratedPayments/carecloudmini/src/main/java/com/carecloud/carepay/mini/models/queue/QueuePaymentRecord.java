package com.carecloud.carepay.mini.models.queue;

import com.orm.SugarRecord;

/**
 * Created by lmenendez on 8/24/17
 */

public class QueuePaymentRecord extends SugarRecord {

    private String paymentRequestId;

    private boolean isRefund = false;

    public QueuePaymentRecord(){}

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public boolean isRefund() {
        return isRefund;
    }

    public void setRefund(boolean refund) {
        isRefund = refund;
    }
}
