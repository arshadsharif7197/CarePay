package com.carecloud.shamrocksdk.payment;


import android.util.Log;

import androidx.annotation.NonNull;

import com.carecloud.shamrocksdk.constants.HttpConstants;
import com.carecloud.shamrocksdk.payment.interfaces.ClientPaymentRequestCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.utils.DeepstreamInstance;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import static com.carecloud.shamrocksdk.utils.AuthorizationUtil.getAuthorization;

import io.deepstream.ConnectionState;
import io.deepstream.ConnectionStateListener;
import io.deepstream.DeepstreamClient;
import io.deepstream.DeepstreamRuntimeErrorHandler;
import io.deepstream.Event;
import io.deepstream.LoginResult;
import io.deepstream.Record;
import io.deepstream.RecordChangedCallback;
import io.deepstream.RecordEventsListener;
import io.deepstream.Topic;

/**
 * Handles subscribing to and tracking changes for payment requests in DeepStream
 */

public class ClientPayment {
    private static final String TAG = ClientPayment.class.getName();

    private static ClientPaymentRequestCallback currentPaymentCallback;


    /**
     * Subscribe to payment request object in DeepStream.
     * Updates to payment request will be received via the callback provided
     *
     * @param userId Shamrock Payments userId
     * @param authToken Shamrock Payments auth token
     * @param paymentRequestId payment request id to subscribe to
     * @param paymentRequestCallback callback for receiving updates to payment request
     */
    public static void trackPaymentRequest(String userId, String authToken, @NonNull String paymentRequestId, @NonNull final ClientPaymentRequestCallback paymentRequestCallback){
        Log.d(TAG, "start handle payment: " + paymentRequestId);
        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorMessage) {
                    paymentRequestCallback.onPaymentConnectionFailure(errorMessage);
                    Log.d(TAG, event.toString() + ": " + errorMessage);
                }
            });

            client.addConnectionChangeListener(new ConnectionStateListener() {
                @Override
                public void connectionStateChanged(ConnectionState connectionState) {
                    Log.d(TAG, "connectionStateChanged: " + connectionState.name());

                }
            });

            Log.d(TAG, "Client created");
            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if(loginResult == null){
                    paymentRequestCallback.onPaymentConnectionFailure("Unable to login client");
                    return;
                }
                if(!loginResult.loggedIn()){
                    paymentRequestCallback.onPaymentConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            Log.d(TAG, "Client connected, getting payment record");
            Record record = client.record.getRecord(paymentRequestId);
            if (record != null) {
                Log.d(TAG, "got payment record: " + paymentRequestId);
                currentPaymentCallback = paymentRequestCallback;

                DeepstreamInstance.subscribeRecord(record, recordChangedCallback, recordEventsListener);

            } else {
                paymentRequestCallback.onPaymentConnectionFailure("Failed to get payment record");
                Log.d(TAG, "Failed to get payment record");
            }


        } catch (Exception e) {
            paymentRequestCallback.onPaymentConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
        }


    }

    /**
     * Un-subscribe from future updates to payment request object
     *
     * @param userId Shamrock Payments userId
     * @param authToken Shamrock Payments auth token
     * @param paymentRequestId payment request id
     */
    public static void releasePaymentRequest(String userId, String authToken, @NonNull String paymentRequestId){
        Log.d(TAG, "Start release payment: "+paymentRequestId);

        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if(loginResult == null || !loginResult.loggedIn()){
                    return;
                }
            }

            releasePaymentRequest(client, paymentRequestId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void releasePaymentRequest(DeepstreamClient client, @NonNull String paymentRequestId){
        Record record = client.record.getRecord(paymentRequestId);
        if(record != null){
            Log.d(TAG, "unsubscibing from payment record: "+paymentRequestId);
            DeepstreamInstance.unSubscribeRecord(record, recordChangedCallback, recordEventsListener);

        }
    }


    private static RecordEventsListener recordEventsListener = new RecordEventsListener() {
        @Override
        public void onError(String string, Event event, String message) {
            Log.d(TAG, event.toString() + ": " + message);
        }

        @Override
        public void onRecordHasProviderChanged(String string, boolean b) {

        }

        @Override
        public void onRecordDeleted(String string) {
            currentPaymentCallback.onPaymentRequestDestroyed(string);
        }

        @Override
        public void onRecordDiscarded(String string) {
            currentPaymentCallback.onPaymentRequestDestroyed(string);
        }
    };

    private static RecordChangedCallback recordChangedCallback = new RecordChangedCallback() {
        @Override
        public void onRecordChanged(String string, JsonElement jsonElement) {
            Log.d(TAG, "Received record changed message: "+string);
            Gson gson = new Gson();
            PaymentRequest paymentRequest = gson.fromJson(jsonElement, PaymentRequest.class);
            if(paymentRequest != null){
                currentPaymentCallback.onPaymentRequestUpdate(string, paymentRequest, jsonElement);
            }else{
                currentPaymentCallback.onPaymentRequestUpdateFail(string, jsonElement);
            }
        }
    };

}
