package com.carecloud.shamrocksdk.payment.interfaces;

import com.carecloud.shamrocksdk.payment.models.RefundRequest;
import com.google.gson.JsonElement;

/**
 * Callback for Clover Devices to handle Refund Request Updates
 */

public interface RefundRequestCallback {

    /**
     * Called when a change is made to the Refund Request object in DeepStream
     * @param refundRequestId refund request id
     * @param refundRequest updated refund request object
     */
    void onRefundRequestUpdate(String refundRequestId, RefundRequest refundRequest);

    /**
     * Called when an update fails for the Refund Request obect in DeepStream
     * @param refundRequestId refund request id
     * @param recordObject raw refund request object
     */
    void onRefundRequestUpdateFail(String refundRequestId, JsonElement recordObject);

    /**
     * Called when DeepStream fails to connect to the Refund Request object
     * @param message error message
     */
    void onRefundConnectionFailure(String message);

    /**
     * Called when Refund Request object is deleted in DeepStream
     * @param refundRequestId refund request id
     */
    void onRefundRequestDestroyed(String refundRequestId);

}
