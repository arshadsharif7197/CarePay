package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataModel {

    @SerializedName("appointment_id")
    @Expose
    private AppointmentModel appointmentId;

    /**
     * 
     * @return
     *     The appointmentId
     */
    public AppointmentModel getAppointmentId() {
        return appointmentId;
    }

    /**
     * 
     * @param appointmentId
     *     The appointment_id
     */
    public void setAppointmentId(AppointmentModel appointmentId) {
        this.appointmentId = appointmentId;
    }

}
