package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSettingDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;

/**
 * Created by lmenendez on 3/2/17
 */
@Deprecated
public interface AppointmentNavigationCallback extends VisitTypeInterface, AvailableHoursInterface,
        DateRangeInterface, ProviderInterface {
    void newAppointment();

    void rescheduleAppointment(AppointmentDTO appointmentDTO);

//    void requestAppointment(AppointmentsSlotsDTO appointmentSlot, String reasonForVisit);

//    void onAppointmentUnconfirmed();

    void onAppointmentRequestSuccess();

//    ApplicationMode getApplicationMode();

//    AppointmentsSettingDTO getAppointmentsSettings();
}
