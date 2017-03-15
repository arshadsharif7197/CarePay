
package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PendingBalancePayloadDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("amount")
    @Expose
    private Double amount;
    @SerializedName("unapplied_credit")
    @Expose
    private double unappliedCredit;
    @SerializedName("details")
    @Expose
    private List<BalanceItemDTO> details = new ArrayList<>();


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<BalanceItemDTO> getDetails() {
        return details;
    }

    public void setDetails(List<BalanceItemDTO> details) {
        this.details = details;
    }

    public double getUnappliedCredit() {
        return unappliedCredit;
    }

    public void setUnappliedCredit(double unappliedCredit) {
        this.unappliedCredit = unappliedCredit;
    }
}
