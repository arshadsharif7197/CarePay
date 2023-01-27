package com.carecloud.shamrocksdk.payment;

import android.content.Context;

import android.util.Log;

import androidx.annotation.NonNull;

import com.carecloud.shamrocksdk.constants.HttpConstants;
import com.carecloud.shamrocksdk.payment.activities.CloverRefundActivity;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.interfaces.RefundRequestCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.RefundRequest;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
import com.carecloud.shamrocksdk.utils.DeepstreamInstance;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
 * Handles making Clover Refunds and updating Refund Requests in DeepStream.
 * This Class should only be used by Clover Devices that can handle making Refunds
 */

public class DeviceRefund {
    private static final String TAG = DeviceRefund.class.getName();

    private static RefundRequestCallback currentPaymentCallback;

    /**
     * Connects to payment request object in DeepStream and starts clover payment.
     * Successful Clover payments will update Payment Request with {@link RefundRequest#setTransactionResponse(JsonObject) Transaction Response}
     *
     * @param context context
     * @param refundRequestId refund request id
     * @param refundRequestCallback callback to handle updates to refund request object
     * @param paymentActionCallback callback to handle refund events
     */
    public static void handleRefundRequest(Context context, @NonNull String refundRequestId, @NonNull final RefundRequestCallback refundRequestCallback, @NonNull final PaymentActionCallback paymentActionCallback) {
        Log.d(TAG, "start handle refund: " + refundRequestId);
        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorMessage) {
                    refundRequestCallback.onRefundConnectionFailure(errorMessage);
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
                LoginResult loginResult = client.login(getAuthorization(context));
                if(loginResult == null){
                    refundRequestCallback.onRefundConnectionFailure("Unable to login client");
                    return;
                }
                if(!loginResult.loggedIn()){
                    refundRequestCallback.onRefundConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            Log.d(TAG, "Client connected, getting payment record");
            Record refundRecord = client.record.getRecord(refundRequestId);
            if (refundRecord != null) {
                Log.d(TAG, "got refund record: " + refundRequestId);
                currentPaymentCallback = refundRequestCallback;

                DeepstreamInstance.subscribeRecord(refundRecord, recordChangedCallback, recordEventsListener);

                Gson gson = new Gson();
                RefundRequest refundRequest = gson.fromJson(refundRecord.get(), RefundRequest.class);

                Record paymentRecord = client.record.getRecord(refundRequest.getPaymentRequestId());
                if(paymentRecord != null) {
                    refundRequest.setState(StateDef.STATE_ACKNOWLEDGED);

                    PaymentRequest paymentRequest = gson.fromJson(paymentRecord.get(), PaymentRequest.class);

                    CloverRefundActivity.newInstance(context, refundRecord, paymentActionCallback, paymentRequest);
                }else{
                    refundRequest.setState(StateDef.STATE_ERRORED);
                }

                refundRecord.set(gson.toJsonTree(refundRequest));

            } else {
                refundRequestCallback.onRefundConnectionFailure("Failed to get payment record");
                Log.d(TAG, "Failed to get payment record");
            }


        } catch (Exception e) {
            refundRequestCallback.onRefundConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
        }

    }

    /**
     * Update Payment Request object in DeepStream
     *
     * @param context context
     * @param refundRequestId refund request id
     * @param refundRequest updated refund request object to update in DeepStream
     * @param refundRequestCallback callback to handle updates to Refund Request
     */
    public static void updateRefundRequest(Context context, String refundRequestId, RefundRequest refundRequest, final RefundRequestCallback refundRequestCallback){
        Log.d(TAG, "start update refund request: " + refundRequestId);
        try{
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorMessage) {
                    refundRequestCallback.onRefundConnectionFailure(errorMessage);
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
                LoginResult loginResult = client.login(getAuthorization(context));
                if(loginResult == null){
                    refundRequestCallback.onRefundConnectionFailure("Unable to login client");
                    return;
                }
                if(!loginResult.loggedIn()){
                    refundRequestCallback.onRefundConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            Log.d(TAG, "Client connected, getting refund record");
            Record record = client.record.getRecord(refundRequestId);
            if(record != null){
                Log.d(TAG, "got refund record: " + refundRequestId);

                Gson gson = new Gson();
                record.set(gson.toJsonTree(refundRequest));

                Record ackRecord = client.record.getRecord(refundRequestId);
                Log.i(TAG, ackRecord.get().toString());
                refundRequestCallback.onRefundRequestUpdate(refundRequestId, gson.fromJson(ackRecord.get(), RefundRequest.class));
            }

        } catch (Exception e) {
            refundRequestCallback.onRefundConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
        }
    }


    /**
     * Un-subscribe from future updates to refund request object
     *
     * @param context context
     * @param refundRequestId refund request id
     */
    public static void releaseRefundRequest(Context context, @NonNull String refundRequestId){
        Log.d(TAG, "Start release refund: "+refundRequestId);
        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(context));
                if(loginResult == null || !loginResult.loggedIn()){
                    return;
                }
            }

            releaseRefundRequest(client, refundRequestId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Un-subscribe from future updates to refund request object
     *
     * @param userId Shamrock Payments userId
     * @param authToken Shamrock Payments auth token
     * @param refundRequestId refund request id
     */
    public static void releaseRefundRequest(String userId, String authToken, @NonNull String refundRequestId){
        Log.d(TAG, "Start release refund: "+refundRequestId);

        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if(loginResult == null || !loginResult.loggedIn()){
                    return;
                }
            }

            releaseRefundRequest(client, refundRequestId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void releaseRefundRequest(DeepstreamClient client, @NonNull String refundRequestId){
        Record record = client.record.getRecord(refundRequestId);
        if(record != null){
            Log.d(TAG, "unsubscibing from payment record: "+refundRequestId);
            DeepstreamInstance.unSubscribeRecord(record, recordChangedCallback, recordEventsListener);
        }
    }

    /**
     * Get a fresh copy of a refund request from deepstream to check the set values
     * @param refundRequestId payment request id
     * @return fresh payment request object or null if error occurs
     */
    public static RefundRequest getRefundAck(String refundRequestId){
        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            Log.d(TAG, "Client created");
            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                return null;
            }

            Log.d(TAG, "Client connected, getting payment record");
            Gson gson = new Gson();
            Record record = client.record.getRecord(refundRequestId);
            return gson.fromJson(record.get(), RefundRequest.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
            currentPaymentCallback.onRefundRequestDestroyed(string);
        }

        @Override
        public void onRecordDiscarded(String string) {
            currentPaymentCallback.onRefundRequestDestroyed(string);
        }
    };

    private static RecordChangedCallback recordChangedCallback = new RecordChangedCallback() {
        @Override
        public void onRecordChanged(String string, JsonElement jsonElement) {
            Log.d(TAG, "Received record changed message: "+string);
            Gson gson = new Gson();
            RefundRequest refundRequest = gson.fromJson(jsonElement, RefundRequest.class);
            if(refundRequest != null){
                currentPaymentCallback.onRefundRequestUpdate(string, refundRequest);
            }else{
                currentPaymentCallback.onRefundRequestUpdateFail(string, jsonElement);
            }
        }
    };

}
