package com.carecloud.carepaylibray.payments.models.postmodel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/20/17.
 */

public class PaymentNewCharge {

    @SerializedName("charge_type")
    private Long chargeType;

    @SerializedName("charge_amount")
    private double amount;

    public Long getChargeType() {
        return chargeType;
    }

    public void setChargeType(Long chargeType) {
        this.chargeType = chargeType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
