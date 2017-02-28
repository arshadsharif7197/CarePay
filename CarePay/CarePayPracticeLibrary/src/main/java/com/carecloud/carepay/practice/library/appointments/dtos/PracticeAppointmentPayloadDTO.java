package com.carecloud.carepay.practice.library.appointments.dtos;

import com.carecloud.carepay.practice.library.checkin.dtos.AppointmentDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cocampo on 2/28/17.
 */
public class PracticeAppointmentPayloadDTO {

    @SerializedName("practice_appointments")
    @Expose
    private AppointmentDTO practiceAppointments = new AppointmentDTO();

    /**
     * @return appointment
     */
    public AppointmentDTO getPracticeAppointments() {
        return practiceAppointments;
    }
}
