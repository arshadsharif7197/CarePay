package com.carecloud.carepaylibray.appointments.models;

import java.io.Serializable;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentModel implements Serializable {

    private String appointmentId;
    private String doctorName;
    private String appointmentTime;
    private String appointmentType;
    private String appointmentDay;
    private String appointmentDate;
    private String placeName;
    private boolean isPending;
    private boolean isCheckedIn;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public boolean isPending() {
        return isPending;
    }

    public void setPending(boolean pending) {
        isPending = pending;
    }

    public boolean isCheckedIn() {
        return isCheckedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        isCheckedIn = checkedIn;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public void setPlaceAddress(String placeAddress) {
        this.placeAddress = placeAddress;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    private String placeAddress;

    public String getButtonTitle() {
        return buttonTitle;
    }

    public void setButtonTitle(String buttonTitle) {
        this.buttonTitle = buttonTitle;
    }

    private String buttonTitle;

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
        this.appointmentId = aptId;
        this.doctorName = doctorName;
        this.appointmentTime = aptTime;
        this.appointmentType = aptType;
        this.appointmentDay = aptDay;
        this.appointmentDate = aptDate;

    }

    public AppointmentModel(String aptId, String doctorName, String aptTime, String aptType, String aptDay, String aptDate, boolean isPending) {
        this.appointmentId = aptId;
        this.doctorName = doctorName;
        this.appointmentTime = aptTime;
        this.appointmentType = aptType;
        this.appointmentDay = aptDay;
        this.appointmentDate = aptDate;
        this.isPending = isPending;
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