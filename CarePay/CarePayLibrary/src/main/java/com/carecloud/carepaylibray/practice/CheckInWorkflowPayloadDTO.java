package com.carecloud.carepaylibray.practice;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kkannan on 5/2/17.
 */

public class CheckInWorkflowPayloadDTO {
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointments = new ArrayList<>();

    /**
     *
     * @return
     * The appointments
     */
    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    /**
     *
     * @param appointments
     * The appointments
     */
    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }
}
