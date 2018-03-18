package com.carecloud.carepaylibray.appointments;

import java.io.Serializable;

/**
 * Created by lmenendez on 5/2/17.
 */

public enum AppointmentDisplayStyle implements Serializable {
    PENDING,
    PENDING_UPCOMING,

    REQUESTED,
    REQUESTED_UPCOMING,

    CHECKED_IN,

    CANCELED,
    CANCELED_UPCOMING,

    CHECKED_OUT,
    MISSED,

    HEADER,

    DEFAULT;

}
