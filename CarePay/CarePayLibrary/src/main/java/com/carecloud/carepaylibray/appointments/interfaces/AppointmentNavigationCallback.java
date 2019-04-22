package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;

/**
 * Created by lmenendez on 3/2/17
 */
public interface AppointmentNavigationCallback extends VideoAppointmentCallback {

    void rescheduleAppointment(AppointmentDTO appointmentDTO);

}
