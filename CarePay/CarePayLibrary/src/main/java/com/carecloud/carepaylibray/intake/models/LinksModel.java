package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinksModel {

    @SerializedName("self")
    @Expose
    private Self self;
    @SerializedName("payments")
    @Expose
    private Payments payments;

    /**
     * 
     * @return
     *     The self
     */
    public Self getSelf() {
        return self;
    }

    /**
     * 
     * @param self
     *     The self
     */
    public void setSelf(Self self) {
        this.self = self;
    }

    /**
     * 
     * @return
     *     The payments
     */
    public Payments getPayments() {
        return payments;
    }

    /**
     * 
     * @param payments
     *     The payments
     */
    public void setPayments(Payments payments) {
        this.payments = payments;
    }

}
