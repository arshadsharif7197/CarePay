package com.carecloud.shamrocksdk.payment;

import android.content.Context;

import android.util.Log;

import androidx.annotation.NonNull;

import com.carecloud.shamrocksdk.constants.HttpConstants;
import com.carecloud.shamrocksdk.payment.activities.CloverPaymentConnectorActivity;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentActionCallback;
import com.carecloud.shamrocksdk.payment.interfaces.PaymentRequestCallback;
import com.carecloud.shamrocksdk.payment.models.PaymentMethod;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
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
 * Handles making Clover Payments and updating Payment Requests in DeepStream.
 * This Class should only be used by Clover Devices that can handle making Payments
 */

public class DevicePayment {
    private static final String PAYMENTS_PATH_PREFIX = "payment_request/";
    private static final String TAG = DevicePayment.class.getName();

    private static PaymentRequestCallback currentPaymentCallback;

    /**
     * Connects to payment request object in DeepStream and starts clover payment.
     * Successful Clover payments will update Payment Request with {@link PaymentRequest#setTransactionResponse(JsonObject) Transaction Response} and {@link PaymentRequest#setPaymentMethod(PaymentMethod) PaymentMethod}
     *
     * @param context                context
     * @param paymentRequestId       payment request id
     * @param paymentRequestCallback callback to handle updates to payment request object
     * @param paymentActionCallback  callback to handle payment events
     */
    public static void handlePaymentRequest(Context context, @NonNull String paymentRequestId, @NonNull final PaymentRequestCallback paymentRequestCallback, @NonNull final PaymentActionCallback paymentActionCallback) {
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
            if (connectionState != ConnectionState.OPEN) {
                LoginResult loginResult = client.login(getAuthorization(context));
                if (loginResult == null) {
                    paymentRequestCallback.onPaymentConnectionFailure("Unable to login client");
                    return;
                }
                if (!loginResult.loggedIn()) {
                    paymentRequestCallback.onPaymentConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            handlePaymentRequest(context, client, paymentRequestId, paymentRequestCallback, paymentActionCallback);

        } catch (Exception e) {
            paymentRequestCallback.onPaymentConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
        }

    }

    /**
     * Connects to payment request object in DeepStream and starts clover payment.
     * Successful Clover payments will update Payment Request with {@link PaymentRequest#setTransactionResponse(JsonObject) Transaction Response} and {@link PaymentRequest#setPaymentMethod(PaymentMethod) PaymentMethod}
     *
     * @param context                context
     * @param userId                 Shamrock Payments userId
     * @param authToken              Shamrock Payments auth token
     * @param paymentRequestId       payment request id
     * @param paymentRequestCallback callback to handle updates to payment request object
     * @param paymentActionCallback  callback to handle payment events
     */
    public static void handlePaymentRequest(Context context, String userId, String authToken, @NonNull String paymentRequestId, @NonNull final PaymentRequestCallback paymentRequestCallback, @NonNull final PaymentActionCallback paymentActionCallback) {
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
            if (connectionState != ConnectionState.OPEN) {
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if (loginResult == null) {
                    paymentRequestCallback.onPaymentConnectionFailure("Unable to login client");
                    return;
                }
                if (!loginResult.loggedIn()) {
                    paymentRequestCallback.onPaymentConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            handlePaymentRequest(context, client, paymentRequestId, paymentRequestCallback, paymentActionCallback);

        } catch (Exception e) {
            paymentRequestCallback.onPaymentConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
        }

    }

    private static void handlePaymentRequest(Context context, DeepstreamClient client, @NonNull String paymentRequestId, @NonNull final PaymentRequestCallback paymentRequestCallback, @NonNull final PaymentActionCallback paymentActionCallback) {
        Log.d(TAG, "Client connected, getting payment record");
        Record record = client.record.getRecord(paymentRequestId);
        if (record != null) {
            Log.d(TAG, "got payment record: " + paymentRequestId);
            currentPaymentCallback = paymentRequestCallback;

            DeepstreamInstance.subscribeRecord(record, recordChangedCallback, recordEventsListener);

            Gson gson = new Gson();
            PaymentRequest paymentRequest = gson.fromJson(record.get(), PaymentRequest.class);
            paymentRequest.setState(StateDef.STATE_ACKNOWLEDGED);

            record.set(gson.toJsonTree(paymentRequest));
            CloverPaymentConnectorActivity.newInstance(context, record, paymentActionCallback);
        } else {
            paymentRequestCallback.onPaymentConnectionFailure("Failed to get payment record");
            Log.d(TAG, "Failed to get payment record");
        }

    }

    /**
     * Update Payment Request object in DeepStream
     *
     * @param context                context
     * @param paymentRequestId       payment request id
     * @param paymentRequest         updated payment request to persist in DeepStream
     * @param paymentRequestCallback callback to handle updates to payment request
     */
    public static void updatePaymentRequest(Context context, String paymentRequestId, PaymentRequest paymentRequest, final PaymentRequestCallback paymentRequestCallback) {
        Log.d(TAG, "start update payment request: " + paymentRequestId);
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
            if (connectionState != ConnectionState.OPEN) {
                LoginResult loginResult = client.login(getAuthorization(context));
                if (loginResult == null) {
                    paymentRequestCallback.onPaymentConnectionFailure("Unable to login client");
                    return;
                }
                if (!loginResult.loggedIn()) {
                    paymentRequestCallback.onPaymentConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            Log.d(TAG, "Client connected, getting payment record");
            Record record = client.record.getRecord(paymentRequestId);
            if (record != null) {
                Log.d(TAG, "got payment record: " + paymentRequestId);

                Gson gson = new Gson();
                record.set(gson.toJsonTree(paymentRequest));

                Record ackRecord = client.record.getRecord(paymentRequestId);
                Log.i(TAG, ackRecord.get().toString());
                paymentRequestCallback.onPaymentRequestUpdate(paymentRequestId, gson.fromJson(ackRecord.get(), PaymentRequest.class));

            }

        } catch (Exception e) {
            paymentRequestCallback.onPaymentConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
        }
    }

    /**
     * Un-subscribe from future updates to payment request object
     *
     * @param context          context
     * @param paymentRequestId payment request id
     */
    public static void releasePaymentRequest(Context context, @NonNull String paymentRequestId) {
        Log.d(TAG, "Start release payment: " + paymentRequestId);
        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            ConnectionState connectionState = client.getConnectionState();
            if (connectionState != ConnectionState.OPEN) {
                LoginResult loginResult = client.login(getAuthorization(context));
                if (loginResult == null || !loginResult.loggedIn()) {
                    return;
                }
            }

            releasePaymentRequest(client, paymentRequestId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Un-subscribe from future updates to payment request object
     *
     * @param userId           Shamrock Payments userId
     * @param authToken        Shamrock Payments auth token
     * @param paymentRequestId payment request id
     */
    public static void releasePaymentRequest(String userId, String authToken, @NonNull String paymentRequestId) {
        Log.d(TAG, "Start release payment: " + paymentRequestId);

        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            ConnectionState connectionState = client.getConnectionState();
            if (connectionState != ConnectionState.OPEN) {
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if (loginResult == null || !loginResult.loggedIn()) {
                    return;
                }
            }

            releasePaymentRequest(client, paymentRequestId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void releasePaymentRequest(DeepstreamClient client, @NonNull String paymentRequestId) {
        Record record = client.record.getRecord(paymentRequestId);
        if (record != null) {
            Log.d(TAG, "unsubscibing from payment record: " + paymentRequestId);
            DeepstreamInstance.unSubscribeRecord(record, recordChangedCallback, recordEventsListener);
        }
    }

    /**
     * Create a new Payment Request in DeepStream and subscribe to future updates
     *
     * @param userId                 Shamrock Payments userId
     * @param authToken              Shamrock Payments auth token
     * @param paymentRequest         payment request object to set in DeepStream
     * @param paymentRequestCallback callback to receive updates to payment request
     */
    public static void createPaymentRequest(String userId, String authToken, PaymentRequest paymentRequest, @NonNull final PaymentRequestCallback paymentRequestCallback) {

        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());
            Log.d(TAG, "Start Payment Request");

            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorMessage) {
                    Log.d(TAG, event + ": " + errorMessage);
                    paymentRequestCallback.onPaymentConnectionFailure(errorMessage);
                }
            });

            client.addConnectionChangeListener(new ConnectionStateListener() {
                @Override
                public void connectionStateChanged(ConnectionState connectionState) {
                    Log.d(TAG, "connectionStateChanged: " + connectionState.name());

                }
            });

            ConnectionState connectionState = client.getConnectionState();
            if (connectionState != ConnectionState.OPEN) {
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if (loginResult == null) {
                    paymentRequestCallback.onPaymentConnectionFailure("Unable to login client");
                    return;
                }
                if (!loginResult.loggedIn()) {
                    paymentRequestCallback.onPaymentConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            String paymentRequestId = client.getUid();
            Log.d(TAG, "Creating payment record: " + paymentRequestId);

            Record record = client.record.getRecord(PAYMENTS_PATH_PREFIX + paymentRequestId);
            if (record != null) {
                Log.d(TAG, "Payment Record Created");

                currentPaymentCallback = paymentRequestCallback;

                DeepstreamInstance.subscribeRecord(record, recordChangedCallback, recordEventsListener);

                Gson gson = new Gson();
                paymentRequest.setState(StateDef.STATE_CREATED);

                record.set(gson.toJsonTree(paymentRequest));
            }


        } catch (Exception e) {
            paymentRequestCallback.onPaymentConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
        }


    }

    /**
     * Get a fresh copy of a payment request from deepstream to check the set values
     *
     * @param paymentRequestId payment request id
     * @return fresh payment request object or null if error occurs
     */
    public static PaymentRequest getPaymentAck(String paymentRequestId) {
        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            Log.d(TAG, "Client created");
            ConnectionState connectionState = client.getConnectionState();
            if (connectionState != ConnectionState.OPEN) {
                return null;
            }

            Log.d(TAG, "Client connected, getting payment record");
            Gson gson = new Gson();
            Record record = client.record.getRecord(paymentRequestId);
            return gson.fromJson(record.get(), PaymentRequest.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Record getPaymentRecord(String paymentRequestId) {
        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            Log.d(TAG, "Client created");
            ConnectionState connectionState = client.getConnectionState();
            if (connectionState != ConnectionState.OPEN) {
                return null;
            }

            Log.d(TAG, "Client connected, getting payment record");
            Gson gson = new Gson();
            Record record = client.record.getRecord(paymentRequestId);
            return record;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void releasePaymentConnection(){
        try {
            DeepstreamInstance.close();
            Log.d(TAG, "Client closed");
        } catch (Exception e) {
            e.printStackTrace();
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
            Log.d(TAG, "Received record changed message: " + string);
            Gson gson = new Gson();
            PaymentRequest paymentRequest = gson.fromJson(jsonElement, PaymentRequest.class);
            if (paymentRequest != null) {
                currentPaymentCallback.onPaymentRequestUpdate(string, paymentRequest);
            } else {
                currentPaymentCallback.onPaymentRequestUpdateFail(string, jsonElement);
            }
        }
    };

}
