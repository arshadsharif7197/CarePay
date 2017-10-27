package com.carecloud.carepaylibray.payments.models.refund;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 10/27/17
 */

public class RefundLineItem {

    @SerializedName("amount")
    private double amount;

    @SerializedName("processing_id")
    private String lineItemId;

    @SerializedName("description")
    private String description;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getLineItemId() {
        return lineItemId;
    }

    public void setLineItemId(String lineItemId) {
        this.lineItemId = lineItemId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
