package com.carecloud.shamrocksdk.payment.models.defs;



import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * State Definitions for Payment Request State
 */

public final class StateDef {
    /**
     * Payment Request has been created and added to DeepStream
     */
    public static final String STATE_CREATED = "Initialized";

    /**
     * Payment Request has been received and acknowledged by Clover processing device
     */
    public static final String STATE_ACKNOWLEDGED = "Acknowledged";

    /**
     * Payment Request is waiting for user action to complete payment
     */
    public static final String STATE_WAITING = "Waiting";

    /**
     * Payment Request has been captured and is ready for processing by Shamrock Payments
     */
    public static final String STATE_CAPTURED = "Captured";

    /**
     * Payment Request has completed processing and no further action will be taken
     */
    public static final String STATE_COMPLETED = "Completed";

    /**
     * Payment Request has been canceled and will not be processed
     */
    public static final String STATE_CANCELED = "Cancelled";

    /**
     * Payment Request is processing or has been queued for re-processing
     */
    public static final String STATE_PROCESSING = "Processing";

    /**
     * Payment Request failed to process or record and has errorred. Manual intervention may be required
     */
    public static final String STATE_ERRORED = "Errored";

    /**
     * Payment request has been processed and is recording
     */
    public static final String STATE_RECORDING  = "Recording";

    public static final String STATE_CAPTURED_WITH_ADJUSTMENT  = "CapturedWithAdjustment";

    @StringDef({STATE_CREATED, STATE_ACKNOWLEDGED, STATE_CAPTURED, STATE_COMPLETED, STATE_CANCELED, STATE_ERRORED, STATE_WAITING, STATE_PROCESSING, STATE_RECORDING, STATE_CAPTURED_WITH_ADJUSTMENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PaymentRequestState{}


}
