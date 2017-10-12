package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;

/**
 * Created by lmenendez on 3/2/17
 */

public interface AppointmentNavigationCallback extends VisitTypeInterface, AvailableHoursInterface,
        DateRangeInterface, ProviderInterface {
    void newAppointment();

    void rescheduleAppointment(AppointmentDTO appointmentDTO);

    void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String comments);

    void onAppointmentUnconfirmed();

    void onAppointmentRequestSuccess();
}
