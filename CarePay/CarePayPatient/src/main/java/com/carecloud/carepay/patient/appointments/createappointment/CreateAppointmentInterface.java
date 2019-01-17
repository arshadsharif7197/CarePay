package com.carecloud.carepay.patient.appointments.createappointment;

import android.support.v4.app.Fragment;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 1/15/19.
 */
public interface CreateAppointmentInterface extends FragmentActivityInterface {
    void showFragment(Fragment fragment);

    void displayToolbar(boolean display, String title);

    void setResourceProvider(AppointmentResourcesItemDTO resource);

    void setVisitType(VisitTypeDTO visitTypeDTO);

    void setLocation(LocationDTO locationDTO);

    void showAvailabilityHourFragment();

    void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO);

    void refreshAppointmentsList();
}
