package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.ResourcesToScheduleDTO;

/**
 * @author pjohnson on 10/10/17.
 */

public interface ProviderInterface {

    void onProviderSelected(AppointmentResourcesDTO appointmentResourcesDTO,
                            AppointmentsResultModel appointmentsResultModel,
                            ResourcesToScheduleDTO resourcesToScheduleDTO);
}
