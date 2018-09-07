package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RetailPostModelOrder {

    @SerializedName("items")
    private List<RetailOrderItem> items = new ArrayList<>();

    @SerializedName("total")
    private double total;

    @SerializedName("subtotal")
    private double subTotal;

    @SerializedName("billing_person")
    private RetailBillingPerson billingPerson = new RetailBillingPerson();

    public List<RetailOrderItem> getItems() {
        return items;
    }

    public void setItems(List<RetailOrderItem> items) {
        this.items = items;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public RetailBillingPerson getBillingPerson() {
        return billingPerson;
    }

    public void setBillingPerson(RetailBillingPerson billingPerson) {
        this.billingPerson = billingPerson;
    }

}
