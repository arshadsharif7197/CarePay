package com.carecloud.carepaylibray.appointments.models;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentModel {

    private String aptId;
    private String doctorName;
    private String aptTime;
    private String aptType;
    private String aptDay;


    public AppointmentModel(String aptId,String doctorName, String aptTime, String aptType, String aptDay) {
        this.aptId = aptId;
        this.doctorName = doctorName;
        this.aptTime = aptTime;
        this.aptType = aptType;
        this.aptDay = aptDay;

    }
    public AppointmentModel() {

    }

    public String getAptId() {
        return aptId;
    }

    public void setAptId(String aptId) {
        this.aptId = aptId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getAptTime() {
        return aptTime;
    }

    public void setAptTime(String aptTime) {
        this.aptTime = aptTime;
    }

    public String getAptType() {
        return aptType;
    }

    public void setAptType(String aptType) {
        this.aptType = aptType;
    }


    public String getAptHeader() {
        return aptDay;
    }

    public void setAptHeader(String aptHeader) {
        this.aptDay = aptDay;
    }

    private String aptHeader;


}