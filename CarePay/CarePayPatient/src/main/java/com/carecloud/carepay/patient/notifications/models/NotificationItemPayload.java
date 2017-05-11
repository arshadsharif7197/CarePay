package com.carecloud.carepay.patient.notifications.models;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationItemPayload {

    @SerializedName("notification_id")
    @Expose
    private String notificationId;

    @SerializedName("notification_type")
    @Expose
    private NotificationType notificationType;

    @SerializedName("read_status")
    @Expose
    private NotificationStatus readStatus;

    @SerializedName("alert")
    @Expose
    private String alertMessage;

    @SerializedName("appointment")
    @Expose
    private AppointmentDTO appointment = new AppointmentDTO();

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(NotificationType notificationType) {
        this.notificationType = notificationType;
    }

    public NotificationStatus getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(NotificationStatus readStatus) {
        this.readStatus = readStatus;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public AppointmentDTO getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentDTO appointment) {
        this.appointment = appointment;
    }
}
