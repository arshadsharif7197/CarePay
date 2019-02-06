package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by harshal_patil on 2/14/2017
 */
public class AppointmentsPrePaymentDTO implements Serializable {
    @SerializedName("visit_type")
    @Expose
    private String visitType;
    @SerializedName("amount")
    @Expose
    private double amount;

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
