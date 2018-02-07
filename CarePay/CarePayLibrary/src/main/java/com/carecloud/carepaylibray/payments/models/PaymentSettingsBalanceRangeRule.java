package com.carecloud.carepaylibray.payments.models;

import android.support.annotation.StringDef;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 1/22/18
 */

public class PaymentSettingsBalanceRangeRule {
    public static final String INTERVAL_MONTHS = "months";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({INTERVAL_MONTHS})
    public @interface IntervalRange {}

    @SerializedName("min_amount")
    @Expose
    private AmountRule minAmount = new AmountRule();

    @SerializedName("max_amount")
    @Expose
    private AmountRule maxAmount = new AmountRule();

    @SerializedName("min_duration")
    @Expose
    private TimeRule minDuration = new TimeRule();

    @SerializedName("max_duration")
    @Expose
    private TimeRule maxDuration = new TimeRule();

    @SerializedName("minimum_balance_required")
    @Expose
    private AmountRule minBalanceRequired = new AmountRule();

    public AmountRule getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(AmountRule minAmount) {
        this.minAmount = minAmount;
    }

    public AmountRule getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(AmountRule maxAmount) {
        this.maxAmount = maxAmount;
    }

    public TimeRule getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(TimeRule minDuration) {
        this.minDuration = minDuration;
    }

    public TimeRule getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(TimeRule maxDuration) {
        this.maxDuration = maxDuration;
    }

    public AmountRule getMinBalanceRequired() {
        return minBalanceRequired;
    }

    public void setMinBalanceRequired(AmountRule minBalanceRequired) {
        this.minBalanceRequired = minBalanceRequired;
    }


    public class AmountRule {

        @SerializedName("value")
        @Expose
        private double value;

        @SerializedName("currency")
        @Expose
        private String currency;

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    public class TimeRule {

        @SerializedName("value")
        @Expose
        private int value;

        @SerializedName("interval")
        @IntervalRange
        private String interval;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @IntervalRange
        public String getInterval() {
            return interval;
        }

        public void setInterval(@IntervalRange String interval) {
            this.interval = interval;
        }
    }
}
