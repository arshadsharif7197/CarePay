package com.carecloud.carepaylibray.fcm;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 7/04/17.
 */
public class NotificationModel {

    @SerializedName("patient_id")
    @Expose
    private String patientId;

    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;

    @SerializedName("practice_id")
    @Expose
    private String practiceId;

    @SerializedName("alert")
    @Expose
    private String alert;

    @SerializedName("appointment_id")
    @Expose
    private String appointmentId;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
