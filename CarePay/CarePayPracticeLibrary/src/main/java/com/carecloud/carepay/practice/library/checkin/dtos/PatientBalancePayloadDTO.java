package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class PatientBalancePayloadDTO {
    @SerializedName("balance_type")
    @Expose
    private String balanceType;
    @SerializedName("unapplied_credit")
    @Expose
    private double unappliedCredit;
    @SerializedName("unbilled")
    @Expose
    private double unbilled;
    @SerializedName("current")
    @Expose
    private double current;
    @SerializedName("greater_than_30")
    @Expose
    private double greaterThan30;
    @SerializedName("greater_than_60")
    @Expose
    private double greaterThan60;
    @SerializedName("greater_than_90")
    @Expose
    private double greaterThan90;
    @SerializedName("greater_than_120")
    @Expose
    private double greaterThan120;
    @SerializedName("total")
    @Expose
    private double total;

    /**
     *
     * @return
     * The balanceType
     */
    public String getBalanceType() {
        return balanceType;
    }

    /**
     *
     * @param balanceType
     * The balance_type
     */
    public void setBalanceType(String balanceType) {
        this.balanceType = balanceType;
    }

    /**
     *
     * @return
     * The unappliedCredit
     */
    public double getUnappliedCredit() {
        return unappliedCredit;
    }

    /**
     *
     * @param unappliedCredit
     * The unapplied_credit
     */
    public void setUnappliedCredit(double unappliedCredit) {
        this.unappliedCredit = unappliedCredit;
    }

    /**
     *
     * @return
     * The unbilled
     */
    public double getUnbilled() {
        return unbilled;
    }

    /**
     *
     * @param unbilled
     * The unbilled
     */
    public void setUnbilled(double unbilled) {
        this.unbilled = unbilled;
    }

    /**
     *
     * @return
     * The current
     */
    public double getCurrent() {
        return current;
    }

    /**
     *
     * @param current
     * The current
     */
    public void setCurrent(double current) {
        this.current = current;
    }

    /**
     *
     * @return
     * The greaterThan30
     */
    public double getGreaterThan30() {
        return greaterThan30;
    }

    /**
     *
     * @param greaterThan30
     * The greater_than_30
     */
    public void setGreaterThan30(double greaterThan30) {
        this.greaterThan30 = greaterThan30;
    }

    /**
     *
     * @return
     * The greaterThan60
     */
    public double getGreaterThan60() {
        return greaterThan60;
    }

    /**
     *
     * @param greaterThan60
     * The greater_than_60
     */
    public void setGreaterThan60(double greaterThan60) {
        this.greaterThan60 = greaterThan60;
    }

    /**
     *
     * @return
     * The greaterThan90
     */
    public double getGreaterThan90() {
        return greaterThan90;
    }

    /**
     *
     * @param greaterThan90
     * The greater_than_90
     */
    public void setGreaterThan90(double greaterThan90) {
        this.greaterThan90 = greaterThan90;
    }

    /**
     *
     * @return
     * The greaterThan120
     */
    public double getGreaterThan120() {
        return greaterThan120;
    }

    /**
     *
     * @param greaterThan120
     * The greater_than_120
     */
    public void setGreaterThan120(double greaterThan120) {
        this.greaterThan120 = greaterThan120;
    }

    /**
     *
     * @return
     * The total
     */
    public double getTotal() {
        return total;
    }

    /**
     *
     * @param total
     * The total
     */
    public void setTotal(double total) {
        this.total = total;
    }
}
