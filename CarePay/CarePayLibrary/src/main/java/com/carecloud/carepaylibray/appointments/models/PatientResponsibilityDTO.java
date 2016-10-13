package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/30/2016.
 */
public class PatientResponsibilityDTO {
    @SerializedName("balance_type")
    @Expose
    private String balanceType;
    @SerializedName("unapplied_credit")
    @Expose
    private Double unappliedCredit;
    @SerializedName("unbilled")
    @Expose
    private Double unbilled;
    @SerializedName("current")
    @Expose
    private Double current;
    @SerializedName("greater_than_30")
    @Expose
    private Double greaterThan30;
    @SerializedName("greater_than_60")
    @Expose
    private Double greaterThan60;
    @SerializedName("greater_than_90")
    @Expose
    private Double greaterThan90;
    @SerializedName("greater_than_120")
    @Expose
    private Double greaterThan120;
    @SerializedName("total")
    @Expose
    private Double total;

    public String getBalanceType() {
        return balanceType;
    }

    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    public Double getUnappliedCredit() {
        return unappliedCredit;
    }

    public void setUnappliedCredit(Double unappliedCredit) {
        this.unappliedCredit = unappliedCredit;
    }

    public Double getUnbilled() {
        return unbilled;
    }

    public void setUnbilled(Double unbilled) {
        this.unbilled = unbilled;
    }

    public Double getCurrent() {
        return current;
    }

    public void setCurrent(Double current) {
        this.current = current;
    }

    public Double getGreaterThan30() {
        return greaterThan30;
    }

    public void setGreaterThan30(Double greaterThan30) {
        this.greaterThan30 = greaterThan30;
    }

    public Double getGreaterThan60() {
        return greaterThan60;
    }

    public void setGreaterThan60(Double greaterThan60) {
        this.greaterThan60 = greaterThan60;
    }

    public Double getGreaterThan90() {
        return greaterThan90;
    }

    public void setGreaterThan90(Double greaterThan90) {
        this.greaterThan90 = greaterThan90;
    }

    public Double getGreaterThan120() {
        return greaterThan120;
    }

    public void setGreaterThan120(Double greaterThan120) {
        this.greaterThan120 = greaterThan120;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
