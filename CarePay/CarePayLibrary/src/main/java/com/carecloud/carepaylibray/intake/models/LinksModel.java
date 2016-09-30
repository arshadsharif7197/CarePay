package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinksModel {

    @SerializedName("self")
    @Expose
    private SelfModel self;
    @SerializedName("payments")
    @Expose
    private PaymentModel payments;

    /**
     * 
     * @return
     *     The self
     */
    public SelfModel getSelf() {
        return self;
    }

    /**
     * 
     * @param self
     *     The self
     */
    public void setSelf(SelfModel self) {
        this.self = self;
    }

    /**
     * 
     * @return
     *     The payments
     */
    public PaymentModel getPayments() {
        return payments;
    }

    /**
     * 
     * @param payments
     *     The payments
     */
    public void setPayments(PaymentModel payments) {
        this.payments = payments;
    }

}
