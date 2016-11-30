package com.carecloud.carepaylibray.payments.models;

/**
 * Created by Rahul on 11/30/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentPatientPlansPayloadDTO {

    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("start_dt")
    @Expose
    private String startDt;
    @SerializedName("end_dt")
    @Expose
    private String endDt;
    @SerializedName("cron_expressions")
    @Expose
    private String cronExpressions;
    @SerializedName("is_enabled")
    @Expose
    private Boolean isEnabled;
    @SerializedName("number_of_cycles")
    @Expose
    private String numberOfCycles;

    /**
     *
     * @return
     * The planType
     */
    public String getPlanType() {
        return planType;
    }

    /**
     *
     * @param planType
     * The plan_type
     */
    public void setPlanType(String planType) {
        this.planType = planType;
    }

    /**
     *
     * @return
     * The amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     * The amount
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     *
     * @return
     * The startDt
     */
    public String getStartDt() {
        return startDt;
    }

    /**
     *
     * @param startDt
     * The start_dt
     */
    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    /**
     *
     * @return
     * The endDt
     */
    public String getEndDt() {
        return endDt;
    }

    /**
     *
     * @param endDt
     * The end_dt
     */
    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    /**
     *
     * @return
     * The cronExpressions
     */
    public String getCronExpressions() {
        return cronExpressions;
    }

    /**
     *
     * @param cronExpressions
     * The cron_expressions
     */
    public void setCronExpressions(String cronExpressions) {
        this.cronExpressions = cronExpressions;
    }

    /**
     *
     * @return
     * The isEnabled
     */
    public Boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     *
     * @param isEnabled
     * The is_enabled
     */
    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    /**
     *
     * @return
     * The numberOfCycles
     */
    public String getNumberOfCycles() {
        return numberOfCycles;
    }

    /**
     *
     * @param numberOfCycles
     * The number_of_cycles
     */
    public void setNumberOfCycles(String numberOfCycles) {
        this.numberOfCycles = numberOfCycles;
    }
}
