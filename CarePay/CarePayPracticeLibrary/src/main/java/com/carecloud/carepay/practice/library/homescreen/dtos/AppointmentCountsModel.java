package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.SerializedName;

public class AppointmentCountsModel implements DTO {

    @SerializedName("payload")
    private AppointmentCountsPayload appointmentCountsPayload = new AppointmentCountsPayload();

    public AppointmentCountsPayload getAppointmentCountsPayload() {
        return appointmentCountsPayload;
    }

    public void setAppointmentCountsPayload(AppointmentCountsPayload appointmentCountsPayload) {
        this.appointmentCountsPayload = appointmentCountsPayload;
    }
}
