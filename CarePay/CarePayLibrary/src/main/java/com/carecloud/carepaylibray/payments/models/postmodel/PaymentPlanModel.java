package com.carecloud.carepaylibray.payments.models.postmodel;

import android.support.annotation.StringDef;

import com.carecloud.carepay.service.library.label.Label;
import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lmenendez on 1/23/18
 */

public class PaymentPlanModel {
    public static final String FREQUENCY_MONTHLY = "monthly";

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({FREQUENCY_MONTHLY})
    public @interface FrequencyDef {}

    @SerializedName("start_dt")
    private String startDate;

    @SerializedName("frequency_code")
    @FrequencyDef
    private String frequencyCode;

    @SerializedName("day_of_month")
    private int dayOfMonth;

    @SerializedName("installments")
    private int installments;

    @SerializedName("amount")
    private double amount;

    @SerializedName("is_enabled")
    private boolean enabled = true;

    @SerializedName("is_cancelled")
    private boolean cancelled = false;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @FrequencyDef
    public String getFrequencyCode() {
        return frequencyCode;
    }

    public void setFrequencyCode(@FrequencyDef String frequencyCode) {
        this.frequencyCode = frequencyCode;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public int getInstallments() {
        return installments;
    }

    public void setInstallments(int installments) {
        this.installments = installments;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getFrequencyString() {
        switch (frequencyCode) {
            case PaymentPlanModel.FREQUENCY_MONTHLY:
            default:
                return Label.getLabel("payment_plan_frequency_month");
        }
    }

}
