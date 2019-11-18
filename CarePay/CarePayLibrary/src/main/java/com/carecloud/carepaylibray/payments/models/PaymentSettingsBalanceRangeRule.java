package com.carecloud.carepaylibray.payments.models;

import androidx.annotation.StringDef;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 1/22/18
 */

public class PaymentSettingsBalanceRangeRule {
    public static final String INTERVAL_MONTHS = "months";
    public static final String INTERVAL_WEEKS = "weeks";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({INTERVAL_MONTHS, INTERVAL_WEEKS})
    public @interface IntervalRange {
    }

    @SerializedName("min_balance")
    @Expose
    private AmountRule minBalance = new AmountRule();

    @SerializedName("max_balance")
    @Expose
    private AmountRule maxBalance = new AmountRule();

    @SerializedName("max_duration")
    @Expose
    private TimeRule maxDuration = new TimeRule();

    @SerializedName("minimum_payment_required")
    @Expose
    private AmountRule minPaymentRequired = new AmountRule();


    public AmountRule getMaxBalance() {
        return maxBalance;
    }

    public void setMaxBalance(AmountRule maxBalance) {
        this.maxBalance = maxBalance;
    }

    public TimeRule getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(TimeRule maxDuration) {
        this.maxDuration = maxDuration;
    }

    public AmountRule getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(AmountRule minBalance) {
        this.minBalance = minBalance;
    }

    public AmountRule getMinPaymentRequired() {
        return minPaymentRequired;
    }

    public void setMinPaymentRequired(AmountRule minPaymentRequired) {
        this.minPaymentRequired = minPaymentRequired;
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
