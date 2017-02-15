package com.carecloud.carepaylibray.appointments.models;


import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentsSettingDTO implements Serializable
{
    @SerializedName("checkin")
    @Expose
    private AppointmentsCheckinDTO checkin;
    @SerializedName("requests")
    @Expose
    private AppointmentsRequestsDTO requests;
    @SerializedName("pre_payments")
    @Expose
    private List<AppointmentsPrePaymentDTO> prePayments = null;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;

    public AppointmentsCheckinDTO getCheckin() {
        return checkin;
    }

    public void setCheckin(AppointmentsCheckinDTO checkin) {
        this.checkin = checkin;
    }

    public AppointmentsRequestsDTO getRequests() {
        return requests;
    }

    public void setRequests(AppointmentsRequestsDTO requests) {
        this.requests = requests;
    }

    public List<AppointmentsPrePaymentDTO> getPrePayments() {
        return prePayments;
    }

    public void setPrePayments(List<AppointmentsPrePaymentDTO> prePayments) {
        this.prePayments = prePayments;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }
}
