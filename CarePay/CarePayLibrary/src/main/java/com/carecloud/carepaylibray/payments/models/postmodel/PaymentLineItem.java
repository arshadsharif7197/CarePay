package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/21/17.
 */

public class PaymentLineItem {

    @SerializedName("description")
    private String description;

    @SerializedName("amount")
    private double amount;

    @SerializedName("metadata")
    private PaymentLineItemMetadata metadata = new PaymentLineItemMetadata();


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentLineItemMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PaymentLineItemMetadata metadata) {
        this.metadata = metadata;
    }


    @Override
    public String toString(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
