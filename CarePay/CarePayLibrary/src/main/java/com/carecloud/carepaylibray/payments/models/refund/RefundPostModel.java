package com.carecloud.carepaylibray.payments.models.refund;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 10/27/17
 */

public class RefundPostModel {

    @SerializedName("deepstream_record_id")
    private String paymentRequestId;

    @SerializedName("line_items")
    private List<RefundLineItem> refundLineItems = new ArrayList<>();

    @SerializedName("external_transaction_response")
    private JsonObject transactionResponse;

    public String getPaymentRequestId() {
        return paymentRequestId;
    }

    public void setPaymentRequestId(String paymentRequestId) {
        this.paymentRequestId = paymentRequestId;
    }

    public List<RefundLineItem> getRefundLineItems() {
        return refundLineItems;
    }

    public void setRefundLineItems(List<RefundLineItem> refundLineItems) {
        this.refundLineItems = refundLineItems;
    }

    public JsonObject getTransactionResponse() {
        return transactionResponse;
    }

    public void setTransactionResponse(JsonObject transactionResponse) {
        this.transactionResponse = transactionResponse;
    }
}
