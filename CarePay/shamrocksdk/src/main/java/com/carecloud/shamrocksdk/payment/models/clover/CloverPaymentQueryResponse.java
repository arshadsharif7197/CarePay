package com.carecloud.shamrocksdk.payment.models.clover;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CloverPaymentQueryResponse {
    @SerializedName("elements")
    private List<JsonElement> payments = new ArrayList<>();


    public List<JsonElement> getPayments() {
        return payments;
    }

    public void setPayments(List<JsonElement> payments) {
        this.payments = payments;
    }
}
