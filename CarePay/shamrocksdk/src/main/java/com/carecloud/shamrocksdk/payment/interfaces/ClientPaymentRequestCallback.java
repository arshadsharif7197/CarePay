package com.carecloud.shamrocksdk.payment.interfaces;

import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.google.gson.JsonElement;

/**
 * Callback for Android Clients to handle Payment Request Updates
 */

public interface ClientPaymentRequestCallback {

    /**
     * Called when a change is made to the Payment Request object in DeepStream
     * @param paymentRequestId payment request id
     * @param paymentRequest updated payment request object
     * @param recordObject raw payment request object
     */
    void onPaymentRequestUpdate(String paymentRequestId, PaymentRequest paymentRequest, JsonElement recordObject);

    /**
     * Called when an update fails for the Payment Request obect in DeepStream
     * @param paymentRequestId payment request id
     * @param recordObject raw payment request object
     */
    void onPaymentRequestUpdateFail(String paymentRequestId, JsonElement recordObject);

    /**
     * Called when DeepStream fails to connect to the Payment Request object
     * @param message error message
     */
    void onPaymentConnectionFailure(String message);

    /**
     * Called when Payment Request object is deleted in DeepStream
     * @param paymentRequestId payment request id
     */
    void onPaymentRequestDestroyed(String paymentRequestId);

}
