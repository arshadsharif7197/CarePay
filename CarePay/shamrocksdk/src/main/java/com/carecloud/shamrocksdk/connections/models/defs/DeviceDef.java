package com.carecloud.shamrocksdk.connections.models.defs;


import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * State definitions for Device States
 */

public final class DeviceDef {
    /**
     * Device is connected to DeepStream and ready to receive a payment or refund request
     */
    public static final String STATE_READY = "ready";

    /**
     * Device is currently processing a payment or refund request and is unavailable
     */
    public static final String STATE_IN_USE = "in_use";

    /**
     * Device is not connected to DeepStream and unavailable for processing requests
     */
    public static final String STATE_OFFLINE = "offline";

    @StringDef({STATE_READY, STATE_IN_USE, STATE_OFFLINE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ConnectionState{}

}
