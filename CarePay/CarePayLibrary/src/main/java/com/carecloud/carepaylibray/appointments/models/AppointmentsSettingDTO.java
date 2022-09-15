package com.carecloud.carepaylibray.appointments.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AppointmentsSettingDTO implements Serializable {
    @SerializedName("checkin")
    @Expose
    private AppointmentsCheckinDTO checkin = new AppointmentsCheckinDTO();
    @SerializedName("schedule_resources_order")
    @Expose
    private ScheduleResourcesOrder scheduleResourcesOrder = new ScheduleResourcesOrder();
    @SerializedName("requests")
    @Expose
    private AppointmentsRequestsDTO requests = new AppointmentsRequestsDTO();
    @SerializedName("pre_payments")
    @Expose
    private List<AppointmentsPrePaymentDTO> prePayments = new ArrayList<>();
    @SerializedName("cancellation_fees")
    @Expose
    private List<AppointmentCancellationFee> cancellationFees = new ArrayList<>();
    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceManagement;
    @SerializedName("charge_cancellation_fees")
    @Expose
    private boolean chargeCancellationFees;
    @SerializedName("custom_text_add_appointment")
    @Expose
    private AppointmentsPopUpDTO appointmentsPopUpDTO;

    public AppointmentsCheckinDTO getCheckin() {
        return checkin;
    }

    public ScheduleResourcesOrder getScheduleResourceOrder() {
        return scheduleResourcesOrder;
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

    public List<AppointmentCancellationFee> getCancellationFees() {
        return cancellationFees;
    }

    public void setCancellationFees(List<AppointmentCancellationFee> cancellationFees) {
        this.cancellationFees = cancellationFees;
    }

    public boolean shouldChargeCancellationFees() {
        return chargeCancellationFees;
    }

    public void setChargeCancellationFees(boolean chargeCancellationFees) {
        this.chargeCancellationFees = chargeCancellationFees;
    }

    public ScheduleResourcesOrder getScheduleResourcesOrder() {
        return scheduleResourcesOrder;
    }

    public void setScheduleResourcesOrder(ScheduleResourcesOrder scheduleResourcesOrder) {
        this.scheduleResourcesOrder = scheduleResourcesOrder;
    }

    public boolean isChargeCancellationFees() {
        return chargeCancellationFees;
    }

    public AppointmentsPopUpDTO getAppointmentsPopUpDTO() {
        return appointmentsPopUpDTO;
    }

    public void setAppointmentsPopUpDTO(AppointmentsPopUpDTO appointmentsPopUpDTO) {
        this.appointmentsPopUpDTO = appointmentsPopUpDTO;
    }
}
