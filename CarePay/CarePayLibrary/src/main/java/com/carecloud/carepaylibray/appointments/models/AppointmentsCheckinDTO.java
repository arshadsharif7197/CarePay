package com.carecloud.carepaylibray.appointments.models;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentsCheckinDTO implements Serializable
{

    @SerializedName("allow_early_checkin")
    @Expose
    private Boolean allowEarlyCheckin;
    @SerializedName("early_checkin_period")
    @Expose
    private String earlyCheckinPeriod;
    @SerializedName("cancellation_notice_period")
    @Expose
    private String cancellationNoticePeriod;

    public Boolean getAllowEarlyCheckin() {
        return allowEarlyCheckin;
    }

    public void setAllowEarlyCheckin(Boolean allowEarlyCheckin) {
        this.allowEarlyCheckin = allowEarlyCheckin;
    }

    public String getEarlyCheckinPeriod() {
        return earlyCheckinPeriod;
    }

    public void setEarlyCheckinPeriod(String earlyCheckinPeriod) {
        this.earlyCheckinPeriod = earlyCheckinPeriod;
    }

    public String getCancellationNoticePeriod() {
        return cancellationNoticePeriod;
    }

    public void setCancellationNoticePeriod(String cancellationNoticePeriod) {
        this.cancellationNoticePeriod = cancellationNoticePeriod;
    }
}
