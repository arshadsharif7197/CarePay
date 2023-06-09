package com.carecloud.carepaylibray.appointments;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.AppointmentsPayloadDTO;

/**
 * Created by lmenendez on 5/11/17.
 */

public class AppointmentDisplayUtil {

    public static AppointmentDisplayStyle determineDisplayStyle(AppointmentsPayloadDTO appointmentsPayload) {
        return determineDisplayStyle(appointmentsPayload, true);
    }

    /**
     * Determint the display style for an appointment object
     *
     * @param appointmentsPayload appointment object
     * @return display style enum
     */
    public static AppointmentDisplayStyle determineDisplayStyle(AppointmentsPayloadDTO appointmentsPayload,
                                                                boolean checkToday) {
        if (appointmentsPayload.getAppointmentStatus().getCode() != null) {
            switch (appointmentsPayload.getAppointmentStatus().getCode()) {
                case CarePayConstants.CHECKED_IN:
                case CarePayConstants.IN_PROGRESS_IN_ROOM:
                case CarePayConstants.IN_PROGRESS_OUT_ROOM:
                    return AppointmentDisplayStyle.CHECKED_IN;
                case CarePayConstants.CHECKED_OUT:
                case CarePayConstants.BILLED:
                case CarePayConstants.MANUALLY_BILLED:
                    return AppointmentDisplayStyle.CHECKED_OUT;
                case CarePayConstants.DENIED:
                    return AppointmentDisplayStyle.DENIED;
                case CarePayConstants.REQUESTED:
                    if (appointmentsPayload.isAppointmentToday()) {
                        return AppointmentDisplayStyle.REQUESTED;
                    } else {
                        return AppointmentDisplayStyle.REQUESTED_UPCOMING;
                    }
                case CarePayConstants.CANCELLED:
                    if (appointmentsPayload.isAppointmentToday()) {
                        return AppointmentDisplayStyle.CANCELED;
                    } else {
                        return AppointmentDisplayStyle.CANCELED_UPCOMING;
                    }
                case CarePayConstants.PENDING:
                case CarePayConstants.CHECKING_IN:
                default: {

                    if (appointmentsPayload.isAppointmentOver() && checkToday) {
                        return AppointmentDisplayStyle.MISSED;
                    } else if (appointmentsPayload.isAppointmentToday()) {
                        return AppointmentDisplayStyle.PENDING;
                    }
                    return AppointmentDisplayStyle.PENDING_UPCOMING;
                }
            }
        } else {
            if (appointmentsPayload.isAppointmentToday()) {
                if (appointmentsPayload.isAppointmentOver()) {
                    return AppointmentDisplayStyle.MISSED;
                }
                return AppointmentDisplayStyle.PENDING;
            }
            return AppointmentDisplayStyle.PENDING_UPCOMING;
        }

    }

}
