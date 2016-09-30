package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayloadModel {

    @SerializedName("payments")
    @Expose
    private Payments_ payments;

    /**
     * 
     * @return
     *     The payments
     */
    public Payments_ getPayments() {
        return payments;
    }

    /**
     * 
     * @param payments
     *     The payments
     */
    public void setPayments(Payments_ payments) {
        this.payments = payments;
    }

}
