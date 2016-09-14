package com.carecloud.carepaylibray.appointments.models;

/**
 * Created by harshal_patil on 9/8/2016.
 */
public class AppointmentModel {

    private String mAptId;
    private String mDoctorName;
    private String mAptTime;
    private String mAptType;
    private String mAptDay;
    private String mAptDate;

    public String getAptDate() {
        return mAptDate;
    }

    public void setAptDate(String aptDate) {
        this.mAptDate = aptDate;
    }

    public String getAptDay() {
        return mAptDay;
    }

    public void setAptDay(String aptDay) {
        this.mAptDay = aptDay;
    }


    public AppointmentModel(String aptId,String doctorName, String aptTime, String aptType, String aptDay,String aptDate) {
        this.mAptDay = aptId;
        this.mDoctorName = doctorName;
        this.mAptTime = aptTime;
        this.mAptType = aptType;
        this.mAptDay = aptDay;
        this.mAptDate = aptDate;

    }
    public AppointmentModel() {

    }

    public String getAptId() {
        return mAptId;
    }

    public void setAptId(String aptId) {
        this.mAptId = aptId;
    }

    public String getDoctorName() {
        return mDoctorName;
    }

    public void setDoctorName(String doctorName) {
        this.mDoctorName = doctorName;
    }

    public String getAptTime() {
        return mAptTime;
    }

    public void setAptTime(String aptTime) {
        this.mAptTime = aptTime;
    }

    public String getAptType() {
        return mAptType;
    }

    public void setAptType(String aptType) {
        this.mAptType = aptType;
    }


    public String getAptHeader() {
        return mAptDay;
    }

    public void setAptHeader(String aptHeader) {
        this.mAptDay = aptHeader;
    }

    private String aptHeader;


}