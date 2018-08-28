package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RetailLineItemOrderItem {

    @SerializedName("sku")
    private String sku;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;

    @SerializedName("selected_options")
    private List<RetailLineItemSelectedOption> selectedOptions = new ArrayList<>();

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<RetailLineItemSelectedOption> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<RetailLineItemSelectedOption> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}
