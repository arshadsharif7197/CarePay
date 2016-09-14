package com.carecloud.carepaylibray.appointments.models;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentModel {

    private String appointmentId;
    private String doctorName;
    private String appointmentTime;
    private String appointmentType;
    private String appointmentDay;
    private String appointmentDate;

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String aptDate) {
        this.appointmentDate = aptDate;
    }

    public String getAppointmentDay() {
        return appointmentDay;
    }

    public void setAppointmentDay(String aptDay) {
        this.appointmentDay = aptDay;
    }


    public AppointmentModel(String aptId,String doctorName, String aptTime, String aptType, String aptDay,String aptDate) {
        this.appointmentDay = aptId;
        this.doctorName = doctorName;
        this.appointmentTime = aptTime;
        this.appointmentType = aptType;
        this.appointmentDay = aptDay;
        this.appointmentDate = aptDate;

    }
    public AppointmentModel() {

    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAptId(String aptId) {
        this.appointmentId = aptId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String aptTime) {
        this.appointmentTime = aptTime;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String aptType) {
        this.appointmentType = aptType;
    }


    public String getAppointmentHeader() {
        return appointmentDay;
    }

    public void setAppointmentHeader(String aptHeader) {
        this.appointmentDay = aptHeader;
    }

    private String appointmentHeader;


}