package com.carecloud.carepaylibray.appointments.createappointment;

import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

/**
 * @author pjohnson on 1/15/19.
 */
public interface CreateAppointmentFragmentInterface {
    void setResourceProvider(AppointmentResourcesItemDTO resource);

    void setVisitType(VisitTypeDTO visitType);

    void setLocation(LocationDTO locationDTO);
}
