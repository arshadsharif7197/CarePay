package com.carecloud.carepaylibray.appointments.models;

public class AppointmentAvailableHoursDto {
    private String mDay;
    private String mMonth;
    private String mDate;
    private String mTimeSlot;

    public AppointmentAvailableHoursDto(String day, String month, String date, String timeSlot) {
        this.mDay = day;
        this.mMonth = month;
        this.mDate = date;
        this.mTimeSlot = timeSlot;
    }

    public String getmDay() {
        return mDay;
    }

    public void setmDay(String mDay) {
        this.mDay = mDay;
    }

    public String getmMonth() {
        return mMonth;
    }

    public void setmMonth(String mMonth) {
        this.mMonth = mMonth;
    }

    public String getmDate() {
        return mDate;
    }

    public void setmDate(String mDate) {
        this.mDate = mDate;
    }

    public String getmTimeSlot() {
        return mTimeSlot;
    }

    public void setmTimeSlot(String mTimeSlot) {
        this.mTimeSlot = mTimeSlot;
    }
}
