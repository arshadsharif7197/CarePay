package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 5/09/17.
 */

public class AppointmentCancellationFee {

    @Expose
    @SerializedName("visit_type")
    private Integer visitType;

    @Expose
    @SerializedName("amount")
    private String amount;

    public Integer getVisitType() {
        return visitType;
    }

    public void setVisitType(Integer visitType) {
        this.visitType = visitType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
