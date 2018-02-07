package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanLineItem {

    @SerializedName("amount")
    private double amount;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    @IntegratedPaymentLineItem.LineItemType
    private String type;

    @SerializedName("type_id")
    private String typeId;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @IntegratedPaymentLineItem.LineItemType
    public String getType() {
        return type;
    }

    public void setType(@IntegratedPaymentLineItem.LineItemType String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }
}
