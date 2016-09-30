package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayloadModel {

    @SerializedName("payments")
    @Expose
    private PaymentsModel payments;

    /**
     * 
     * @return
     *     The payments
     */
    public PaymentsModel getPayments() {
        return payments;
    }

    /**
     * 
     * @param payments
     *     The payments
     */
    public void setPayments(PaymentsModel payments) {
        this.payments = payments;
    }

}
