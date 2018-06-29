package com.carecloud.carepay.mini.models.queue;

import com.google.gson.JsonElement;
import com.orm.SugarRecord;

/**
 * Created by lmenendez on 8/24/17
 */

public class QueuePaymentRecord extends SugarRecord {

    private String paymentRequestId;

    private boolean isRefund = false;

    private JsonElement requestObject;

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

    public JsonElement getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(JsonElement requestObject) {
        this.requestObject = requestObject;
    }
}
