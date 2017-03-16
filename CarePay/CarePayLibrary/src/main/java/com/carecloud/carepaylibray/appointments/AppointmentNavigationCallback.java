package com.carecloud.carepaylibray.appointments;

import com.carecloud.carepaylibray.appointments.models.AppointmentAvailabilityDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

import java.util.Date;

/**
 * Created by lmenendez on 3/2/17.
 */

public interface AppointmentNavigationCallback {
    void newAppointment();

    void rescheduleAppointment(AppointmentDTO appointmentDTO);

    void selectVisitType(AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel);

    void selectTime(VisitTypeDTO visitTypeDTO, AppointmentResourcesDTO appointmentResourcesDTO, AppointmentsResultModel appointmentsResultModel);

    void selectTime(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel);

    void selectDateRange(Date startDate, Date endDate, VisitTypeDTO visitTypeDTO, AppointmentResourcesItemDTO appointmentResource, AppointmentsResultModel appointmentsResultModel);

    void confirmAppointment(String startTime, String endTime, AppointmentAvailabilityDTO availabilityDTO);

    void requestAppointment(String startTime, String endTime, String comments);

    void onAppointmentUnconfirmed();

    void onAppointmentRequestSuccess();
}
