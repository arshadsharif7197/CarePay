package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.SerializedName;

public class AppointmentCountsPayload {

    @SerializedName("appointment_counts")
    private HomeScreenAppointmentCountsDTO appointmentCounts = new HomeScreenAppointmentCountsDTO();

    public HomeScreenAppointmentCountsDTO getAppointmentCounts() {
        return appointmentCounts;
    }

    public void setAppointmentCounts(HomeScreenAppointmentCountsDTO appointmentCounts) {
        this.appointmentCounts = appointmentCounts;
    }
}
