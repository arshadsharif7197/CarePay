package com.carecloud.carepay.practice.library.appointments.dtos;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cocampo on 2/28/17
 */
public class PracticeAppointmentPayloadDTO {

    @SerializedName(value = "practice_appointments", alternate = "appointments")
    @Expose
    private AppointmentDTO practiceAppointments = new AppointmentDTO();

    /**
     * @return appointment
     */
    public AppointmentDTO getPracticeAppointments() {
        return practiceAppointments;
    }
}
