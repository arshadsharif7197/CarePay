package com.carecloud.shamrocksdk.payment.interfaces;

import android.content.Context;

import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.google.gson.JsonElement;

/**
 * Callback to handle payment processing on Clover device
 */

public interface PaymentActionCallback {

    /**
     * Called when payment processing is started
     * @param paymentRequestId payment request id
     */
    void onPaymentStarted(String paymentRequestId);

    /**
     * Called when payment processing is completed
     * @param paymentRequestId payment request id
     */
    void onPaymentComplete(String paymentRequestId, JsonElement requestObject);

    /**
     * Called when payment is processed but fails to update payment request in DeepStream.
     * Clients should queue and retry updating payment request using {@link com.carecloud.shamrocksdk.payment.DevicePayment#updatePaymentRequest(Context, String, PaymentRequest, PaymentRequestCallback) updatePaymentRequest}
     * @param paymentRequestId payment request id
     * @param paymentPayload payment payload to update deepstream object
     * @param errorMessage error message
     */
    void onPaymentCompleteWithError(String paymentRequestId, JsonElement paymentPayload, String errorMessage);

    /**
     * Called when payment is cancelled in Clover Payment application
     * @param paymentRequestId payment request id
     * @param message cancel message
     */
    void onPaymentCanceled(String paymentRequestId, String message);

    /**
     * Called when payment fails to process due to an error
     * @param paymentRequestId payment request id
     * @param message error message
     */
    void onPaymentFailed(String paymentRequestId, String message);

    /**
     * Called when there is a failure due to connection errors
     * @param message error message
     */
    void onConnectionFailed(String message);

}
