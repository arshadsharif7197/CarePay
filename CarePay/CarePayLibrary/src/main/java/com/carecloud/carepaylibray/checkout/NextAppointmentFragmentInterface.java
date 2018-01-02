package com.carecloud.carepaylibray.checkout;

import com.carecloud.carepaylibray.appointments.models.AppointmentsSlotsDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;

/**
 * @author pjohnson on 10/10/17.
 */

public interface NextAppointmentFragmentInterface {

    void setLocationAndTime(AppointmentsSlotsDTO appointmentsSlot, boolean showNextDialogs);

    boolean setVisitType(VisitTypeDTO visitTypeDTO, boolean showNextDialogs);

    void setSelectedProvider(ProviderDTO provider, boolean showNextDialogs);
}
