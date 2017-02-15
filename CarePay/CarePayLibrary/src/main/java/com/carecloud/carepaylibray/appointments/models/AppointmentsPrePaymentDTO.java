package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by harshal_patil on 2/14/2017.
 */
public class AppointmentsPrePaymentDTO implements Serializable {
    @SerializedName("visit_type")
    @Expose
    private Integer visitType;
    @SerializedName("amount")
    @Expose
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
