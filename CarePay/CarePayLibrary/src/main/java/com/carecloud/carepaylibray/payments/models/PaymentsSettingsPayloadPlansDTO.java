package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsSettingsPayloadPlansDTO {

    @SerializedName("default_payment")
    @Expose
    private String defaultPayment;
    @SerializedName("minimum_payment")
    @Expose
    private String minimumPayment;
    @SerializedName("maximum_payment")
    @Expose
    private String maximumPayment;
    @SerializedName("default_cycles_months")
    @Expose
    private String defaultCyclesMonths;
    @SerializedName("minimum_cycles_months")
    @Expose
    private String minimumCyclesMonths;
    @SerializedName("maximum_cycles_months")
    @Expose
    private String maximumCyclesMonths;

    /**
     *
     * @return
     * The defaultPayment
     */
    public String getDefaultPayment() {
        return defaultPayment;
    }

    /**
     *
     * @param defaultPayment
     * The default_payment
     */
    public void setDefaultPayment(String defaultPayment) {
        this.defaultPayment = defaultPayment;
    }

    /**
     *
     * @return
     * The minimumPayment
     */
    public String getMinimumPayment() {
        return minimumPayment;
    }

    /**
     *
     * @param minimumPayment
     * The minimum_payment
     */
    public void setMinimumPayment(String minimumPayment) {
        this.minimumPayment = minimumPayment;
    }

    /**
     *
     * @return
     * The maximumPayment
     */
    public String getMaximumPayment() {
        return maximumPayment;
    }

    /**
     *
     * @param maximumPayment
     * The maximum_payment
     */
    public void setMaximumPayment(String maximumPayment) {
        this.maximumPayment = maximumPayment;
    }

    /**
     *
     * @return
     * The defaultCyclesMonths
     */
    public String getDefaultCyclesMonths() {
        return defaultCyclesMonths;
    }

    /**
     *
     * @param defaultCyclesMonths
     * The default_cycles_months
     */
    public void setDefaultCyclesMonths(String defaultCyclesMonths) {
        this.defaultCyclesMonths = defaultCyclesMonths;
    }

    /**
     *
     * @return
     * The minimumCyclesMonths
     */
    public String getMinimumCyclesMonths() {
        return minimumCyclesMonths;
    }

    /**
     *
     * @param minimumCyclesMonths
     * The minimum_cycles_months
     */
    public void setMinimumCyclesMonths(String minimumCyclesMonths) {
        this.minimumCyclesMonths = minimumCyclesMonths;
    }

    /**
     *
     * @return
     * The maximumCyclesMonths
     */
    public String getMaximumCyclesMonths() {
        return maximumCyclesMonths;
    }

    /**
     *
     * @param maximumCyclesMonths
     * The maximum_cycles_months
     */
    public void setMaximumCyclesMonths(String maximumCyclesMonths) {
        this.maximumCyclesMonths = maximumCyclesMonths;
    }
}
