package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;

/**
 * Created by lmenendez on 3/2/17
 */

public interface AppointmentNavigationCallback extends VisitTypeInterface, AvailableHoursInterface, DateRangeInterface {
    void newAppointment();

    void rescheduleAppointment(AppointmentDTO appointmentDTO);

    void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel, ResourcesToScheduleDTO resourcesToScheduleDTO);

    void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String comments);

    void onAppointmentUnconfirmed();

    void onAppointmentRequestSuccess();
}
