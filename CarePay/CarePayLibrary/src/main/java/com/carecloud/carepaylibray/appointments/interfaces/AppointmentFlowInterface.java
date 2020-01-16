package com.carecloud.carepaylibray.appointments.interfaces;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.interfaces.FragmentActivityInterface;

/**
 * @author pjohnson on 2020-01-16.
 */
public interface AppointmentFlowInterface extends FragmentActivityInterface {

    void onCheckOutStarted(AppointmentDTO appointmentDTO);
}
