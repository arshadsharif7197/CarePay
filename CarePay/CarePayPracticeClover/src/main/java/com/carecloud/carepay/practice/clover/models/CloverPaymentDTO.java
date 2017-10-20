package com.carecloud.carepay.practice.clover.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/20/17.
 */

public class CloverPaymentDTO {

    @SerializedName("result")
    @Expose
    private String result;

    @SerializedName("offline")
    @Expose
    private boolean offline = false;

    @SerializedName("cardTransaction")
    @Expose
    private CloverCardTransactionInfo cloverCardTransactionInfo = new CloverCardTransactionInfo();

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("order")
    @Expose
    private Order order = new Order();

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public CloverCardTransactionInfo getCloverCardTransactionInfo() {
        return cloverCardTransactionInfo;
    }

    public void setCloverCardTransactionInfo(CloverCardTransactionInfo cloverCardTransactionInfo) {
        this.cloverCardTransactionInfo = cloverCardTransactionInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public class Order {

        @SerializedName("id")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
