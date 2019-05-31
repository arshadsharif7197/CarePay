package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;

public interface VideoAppointmentCallback {

    void startVideoVisit(AppointmentDTO appointmentDTO);

}
