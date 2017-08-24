package com.carecloud.carepaylibray.appointments.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsSettingDTO implements Serializable
{
    @SerializedName("checkin")
    @Expose
    private AppointmentsCheckinDTO checkin = new AppointmentsCheckinDTO();
    @SerializedName("requests")
    @Expose
    private AppointmentsRequestsDTO requests = new AppointmentsRequestsDTO();
    @SerializedName("pre_payments")
    @Expose
    private transient List<AppointmentsPrePaymentDTO> prePayments = new ArrayList<>();
    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceManagement;

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

    public String getPracticeManagement() {
        return practiceManagement;
    }

    public void setPracticeManagement(String practiceManagement) {
        this.practiceManagement = practiceManagement;
    }
}
