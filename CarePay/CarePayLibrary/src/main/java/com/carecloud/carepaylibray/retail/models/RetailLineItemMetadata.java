package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RetailLineItemMetadata {

    @SerializedName("ecwid_order")
    private RetailLineItemOrder order = new RetailLineItemOrder();

    public RetailLineItemOrder getOrder() {
        return order;
    }

    public void setOrder(RetailLineItemOrder order) {
        this.order = order;
    }

    public class RetailLineItemOrder {

        @SerializedName("items")
        private List<RetailLineItemOrderItem> items = new ArrayList<>();

        @SerializedName("total")
        private double total;

        @SerializedName("subtotal")
        private double subTotal;

        public List<RetailLineItemOrderItem> getItems() {
            return items;
        }

        public void setItems(List<RetailLineItemOrderItem> items) {
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
    }
}
