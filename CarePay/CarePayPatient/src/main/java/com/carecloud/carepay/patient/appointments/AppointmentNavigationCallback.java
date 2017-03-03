package com.carecloud.carepay.patient.appointments;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

/**
 * Created by lmenendez on 3/2/17.
 */

public interface AppointmentNavigationCallback {
    void newAppointment();

    void rescheduleAppointment(AppointmentDTO appointmentDTO);

    void availableTimes(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO);
}
