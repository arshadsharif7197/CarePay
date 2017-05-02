package com.carecloud.carepaylibray.appointments;

import java.io.Serializable;

/**
 * Created by lmenendez on 5/2/17.
 */

public enum AppointmentDisplayStyle implements Serializable {
    PENDING,
    REQUESTED,
    CHECKED_IN,

    CANCELED,
    FUTURE,
    CHECKED_OUT,
    MISSED,

    DEFAULT;

}
