package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 26/07/18.
 */
public class PaymentSettingFrequencyCode {

    @Expose
    @SerializedName("weekly")
    private FrequencyCodeDTO weekly = new FrequencyCodeDTO();

    @Expose
    @SerializedName("monthly")
    private FrequencyCodeDTO monthly = new FrequencyCodeDTO();

    public FrequencyCodeDTO getWeekly() {
        return weekly;
    }

    public void setWeekly(FrequencyCodeDTO weekly) {
        this.weekly = weekly;
    }

    public FrequencyCodeDTO getMonthly() {
        return monthly;
    }

    public void setMonthly(FrequencyCodeDTO monthly) {
        this.monthly = monthly;
    }
}
