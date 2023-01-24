package com.carecloud.carepaylibray.appointments.models;

import java.io.Serializable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentsCheckinDTO implements Serializable {

    @SerializedName("allow_early_checkin")
    @Expose
    private Boolean allowEarlyCheckin;

    @SerializedName("early_checkin_period")
    @Expose
    private String earlyCheckinPeriod;

    @SerializedName("cancellation_notice_period")
    @Expose
    private String cancellationNoticePeriod;

    @SerializedName("require_checkin_telehealth")
    @Expose
    private boolean require_checkin_telehealth;

    @SerializedName("move_patient_pre_registration")
    @Expose
    private boolean move_patient_pre_registration;

    @SerializedName("move_patient_to_registrations_before_scheduling_an_apointment")
    @Expose
    private boolean move_patient_to_registrations_before_scheduling_an_appointment;

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

    public boolean isRequire_checkin_telehealth() {
        return require_checkin_telehealth;
    }

    public void setRequire_checkin_telehealth(boolean require_checkin_telehealth) {
        this.require_checkin_telehealth = require_checkin_telehealth;
    }

    public boolean isMove_patient_pre_registration() {
        return move_patient_pre_registration;
    }

    public void setMove_patient_pre_registration(boolean move_patient_pre_registration) {
        this.move_patient_pre_registration = move_patient_pre_registration;
    }

    public boolean isMove_patient_to_registrations_before_scheduling_an_appointment() {
        return move_patient_to_registrations_before_scheduling_an_appointment;
    }

    public void setMove_patient_to_registrations_before_scheduling_an_appointment(boolean move_patient_to_registrations_before_scheduling_an_appointment) {
        this.move_patient_to_registrations_before_scheduling_an_appointment = move_patient_to_registrations_before_scheduling_an_appointment;
    }
}
