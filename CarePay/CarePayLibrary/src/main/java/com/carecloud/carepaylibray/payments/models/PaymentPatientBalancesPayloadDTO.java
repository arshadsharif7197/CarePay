package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentPatientBalancesPayloadDTO {

    @SerializedName("balance_type")
    @Expose
    private String balanceType;
    @SerializedName("unapplied_credit")
    @Expose
    private String unappliedCredit;
    @SerializedName("unbilled")
    @Expose
    private String unbilled;
    @SerializedName("current")
    @Expose
    private String current;
    @SerializedName("greater_than_30")
    @Expose
    private String greaterThan30;
    @SerializedName("greater_than_60")
    @Expose
    private String greaterThan60;
    @SerializedName("greater_than_90")
    @Expose
    private String greaterThan90;
    @SerializedName("greater_than_120")
    @Expose
    private String greaterThan120;
    @SerializedName("total")
    @Expose
    private String total;

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
    public String getUnappliedCredit() {
        return unappliedCredit;
    }

    /**
     *
     * @param unappliedCredit
     * The unapplied_credit
     */
    public void setUnappliedCredit(String unappliedCredit) {
        this.unappliedCredit = unappliedCredit;
    }

    /**
     *
     * @return
     * The unbilled
     */
    public String getUnbilled() {
        return unbilled;
    }

    /**
     *
     * @param unbilled
     * The unbilled
     */
    public void setUnbilled(String unbilled) {
        this.unbilled = unbilled;
    }

    /**
     *
     * @return
     * The current
     */
    public String getCurrent() {
        return current;
    }

    /**
     *
     * @param current
     * The current
     */
    public void setCurrent(String current) {
        this.current = current;
    }

    /**
     *
     * @return
     * The greaterThan30
     */
    public String getGreaterThan30() {
        return greaterThan30;
    }

    /**
     *
     * @param greaterThan30
     * The greater_than_30
     */
    public void setGreaterThan30(String greaterThan30) {
        this.greaterThan30 = greaterThan30;
    }

    /**
     *
     * @return
     * The greaterThan60
     */
    public String getGreaterThan60() {
        return greaterThan60;
    }

    /**
     *
     * @param greaterThan60
     * The greater_than_60
     */
    public void setGreaterThan60(String greaterThan60) {
        this.greaterThan60 = greaterThan60;
    }

    /**
     *
     * @return
     * The greaterThan90
     */
    public String getGreaterThan90() {
        return greaterThan90;
    }

    /**
     *
     * @param greaterThan90
     * The greater_than_90
     */
    public void setGreaterThan90(String greaterThan90) {
        this.greaterThan90 = greaterThan90;
    }

    /**
     *
     * @return
     * The greaterThan120
     */
    public String getGreaterThan120() {
        return greaterThan120;
    }

    /**
     *
     * @param greaterThan120
     * The greater_than_120
     */
    public void setGreaterThan120(String greaterThan120) {
        this.greaterThan120 = greaterThan120;
    }

    /**
     *
     * @return
     * The total
     */
    public String getTotal() {
        return total;
    }

    /**
     *
     * @param total
     * The total
     */
    public void setTotal(String total) {
        this.total = total;
    }
}
