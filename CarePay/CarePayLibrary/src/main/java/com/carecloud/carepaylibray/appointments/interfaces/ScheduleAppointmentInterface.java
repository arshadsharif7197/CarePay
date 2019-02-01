package com.carecloud.carepaylibray.appointments.interfaces;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ScheduleAppointmentRequestDTO;
import com.carecloud.carepaylibray.appointments.models.VisitTypeDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 1/15/19.
 */
public interface ScheduleAppointmentInterface extends FragmentActivityInterface, DateCalendarRangeInterface {
    void showFragment(DialogFragment fragment);

    void displayToolbar(boolean display, String title);

    void setResourceProvider(AppointmentResourcesItemDTO resource);

    void setVisitType(VisitTypeDTO visitTypeDTO);

    void setLocation(LocationDTO locationDTO);

    void showAvailabilityHourFragment();

    void showAppointmentConfirmationFragment(AppointmentDTO appointmentDTO);

    void appointmentScheduledSuccessfully();

    void rescheduleAppointment(AppointmentDTO appointmentDTO);

    void startPrepaymentProcess(ScheduleAppointmentRequestDTO appointmentRequestDto, double amount, String practiceId);
}
