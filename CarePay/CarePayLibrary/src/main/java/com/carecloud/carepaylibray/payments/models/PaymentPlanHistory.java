package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 1/25/18
 */

public class PaymentPlanHistory {

    @SerializedName("amount")
    @Expose
    private double amount;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("items")
    @Expose
    private List<PaymentPlanLineItem> paymentPlanLineItems = new ArrayList<>();

    @SerializedName("one_time_payment")
    @Expose
    private boolean oneTimePayment = false;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<PaymentPlanLineItem> getPaymentPlanLineItems() {
        return paymentPlanLineItems;
    }

    public void setPaymentPlanLineItems(List<PaymentPlanLineItem> paymentPlanLineItems) {
        this.paymentPlanLineItems = paymentPlanLineItems;
    }

    public boolean isOneTimePayment() {
        return oneTimePayment;
    }

    public void setOneTimePayment(boolean oneTimePayment) {
        this.oneTimePayment = oneTimePayment;
    }
}
