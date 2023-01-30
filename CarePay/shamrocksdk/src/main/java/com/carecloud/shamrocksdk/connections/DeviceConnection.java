package com.carecloud.shamrocksdk.connections;

import android.content.Context;

import android.util.Log;

import androidx.annotation.NonNull;

import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ConnectionCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.carecloud.shamrocksdk.connections.models.defs.DeviceDef;
import com.carecloud.shamrocksdk.constants.HttpConstants;
import com.carecloud.shamrocksdk.payment.models.PaymentRequest;
import com.carecloud.shamrocksdk.payment.models.defs.StateDef;
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
 * Handles connecting and disconnecting device to DeepStream as well as subscribing to changes in the device record.
 */
public class DeviceConnection {
    private static final String TAG = DeviceConnection.class.getName();
    private static final String DEVICE_PATH_PREFIX = "device/";

    private static ConnectionActionCallback currentActionCallback;


    /**
     * Connect Device to DeepStream and subscribe to Device updates
     *
     * @param context Context
     * @param deviceId Device Id from Registration
     * @param connectionCallback Callback for connection result
     * @param connectionActionCallback Ongoing Callback for Device updates
     */
    public static void connect(Context context, String deviceId, @NonNull final ConnectionCallback connectionCallback, @NonNull ConnectionActionCallback connectionActionCallback) {
        connectionCallback.startDeviceConnection();

        try {
            DeepstreamInstance.close();
            final DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorMessage) {
                    connectionCallback.onConnectionFailure(errorMessage);
                    Log.w(TAG, event.toString() + ": " +errorMessage);
                    client.close();
                }
            });

            client.addConnectionChangeListener(loggingStateChangeListener) ;

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(context));
                if(loginResult == null){
                    connectionCallback.onConnectionFailure("Unable to login client");
                    return;
                }
                if(!loginResult.loggedIn()){
                    connectionCallback.onConnectionFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            Log.d(DeepstreamInstance.class.getName(), "Current Instance State: "+client.getConnectionState().toString());

            Record record = client.record.getRecord(DEVICE_PATH_PREFIX + deviceId);
            if (record != null) {
                currentActionCallback = connectionActionCallback;

                DeepstreamInstance.subscribeRecord(record, recordChangedCallback, recordEventsListener);

                Log.d(TAG, "Record Name: " + record.name());
                Gson gson = new Gson();
                Device deviceResult = gson.fromJson(record.get(), Device.class);
                if(deviceResult.getPaymentRequestId() != null){
                    Record paymentRecord = client.record.getRecord(deviceResult.getPaymentRequestId());
                    if(paymentRecord != null) {
                        PaymentRequest paymentRequest = gson.fromJson(paymentRecord.get(), PaymentRequest.class);
                        switch (paymentRequest.getState()){
                            case StateDef.STATE_ACKNOWLEDGED:
                            case StateDef.STATE_CREATED:
                            case StateDef.STATE_WAITING:
                                Log.d(TAG, "Cancelling this payment request.. appear to not have been captured");
                                // handle either Payment Request or Refund requests by setting state manually to prevent overriding other fields
                                paymentRecord.set("state", StateDef.STATE_CANCELED);
//                                paymentRequest.setState(StateDef.STATE_CANCELED);
//                                paymentRecord.set(gson.toJsonTree(paymentRequest));
                                break;
                            default:
                                Log.d(TAG, "Maintaining payment request state, don't update it");
                        }
                    }
                }

                deviceResult.setState(DeviceDef.STATE_READY);
                deviceResult.setPaymentRequestId(null);
                deviceResult.setRefunding(false);
                deviceResult.setLastUpdated();

                record.set(gson.toJsonTree(deviceResult));
                Log.d(TAG, record.get().toString());

                connectionCallback.onDeviceConnected(deviceResult);

            }else{
                connectionCallback.onConnectionFailure("Unable to retrieve Device Record");
            }

        } catch (Exception e) {
            connectionCallback.onConnectionFailure("Device Failed, Please retry");
            e.printStackTrace();
            DeepstreamInstance.close();
        }

    }


    /**
     * Disconnect device from DeepStream and unsubscribe from further updates to Device record
     *
     * @param context Context
     * @param deviceId DeviceId from Registration
     */
    public static void disconnect(Context context, String deviceId, final ConnectionCallback connectionCallback) {

        try {
            final DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorMessage) {
                    Log.w(TAG, event.toString() + ": " +errorMessage);
                    client.close();
                }
            });

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState == ConnectionState.CLOSED){
                Log.w(TAG, "Connection already closed");
                return;
            }

            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(context));
                if(loginResult == null || !loginResult.loggedIn()){
                    return;
                }
            }

            if(connectionCallback != null) {
                client.addConnectionChangeListener(new ConnectionStateListener() {
                    @Override
                    public void connectionStateChanged(ConnectionState connectionState) {
                        if (connectionState == ConnectionState.CLOSED) {
                            connectionCallback.onDeviceDisconnected(null);
                        }
                    }
                });
            }


            Record record = client.record.getRecord(DEVICE_PATH_PREFIX + deviceId);
            if (record != null) {
                Log.d(TAG, "Record Name: " + record.name());

                DeepstreamInstance.unSubscribeRecord(record, recordChangedCallback, recordEventsListener);

                Gson gson = new Gson();
                Device deviceResult = gson.fromJson(record.get(), Device.class);
                if(deviceResult.getPaymentRequestId() != null){
                    Record paymentRecord = client.record.getRecord(deviceResult.getPaymentRequestId());
                    if(paymentRecord != null) {
                        PaymentRequest paymentRequest = gson.fromJson(paymentRecord.get(), PaymentRequest.class);
                        paymentRequest.setState(StateDef.STATE_CANCELED);
                        paymentRecord.set(gson.toJsonTree(paymentRequest));
                    }
                }
                deviceResult.setState(DeviceDef.STATE_OFFLINE);
                deviceResult.setPaymentRequestId(null);
                deviceResult.setLastUpdated();

                record.set(gson.toJsonTree(deviceResult));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DeepstreamInstance.close();
        }

    }

    /**
     * Update device in DeepStream
     *
     * @param context Context
     * @param deviceId Device Id from Registration
     * @param updatedDevice updated Device object
     * @return true if DeepStream was successfully updated, false if any error occurs
     */
    public static boolean updateConnection(Context context, String deviceId, Device updatedDevice){

        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(context));
                if(loginResult == null || !loginResult.loggedIn()){
                    return false;
                }
            }

            Record record = client.record.getRecord(DEVICE_PATH_PREFIX + deviceId);
            if (record != null) {
                Log.d(TAG, "Record Name: " + record.name());
                updatedDevice.setLastUpdated();


                Gson gson = new Gson();
                record.set(gson.toJsonTree(updatedDevice));

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private static RecordEventsListener recordEventsListener = new RecordEventsListener() {
        @Override
        public void onError(String string, Event event, String message) {
            currentActionCallback.onConnectionError(string, event.name(), message);
        }

        @Override
        public void onRecordHasProviderChanged(String string, boolean hasProvider) {

        }

        @Override
        public void onRecordDeleted(String string) {
            currentActionCallback.onConnectionDestroyed(string);
        }

        @Override
        public void onRecordDiscarded(String string) {
            currentActionCallback.onConnectionDestroyed(string);
        }
    };


    private static RecordChangedCallback recordChangedCallback = new RecordChangedCallback() {
        @Override
        public void onRecordChanged(String string, JsonElement jsonElement) {
            Gson gson = new Gson();
            Device device = gson.fromJson(jsonElement, Device.class);
            if(device != null){
                currentActionCallback.onConnectionUpdate(string, device);
            }else{
                currentActionCallback.onConnectionUpdateFail(string, jsonElement);
            }
        }
    };


    private static ConnectionStateListener loggingStateChangeListener =  new ConnectionStateListener() {
        @Override
        public void connectionStateChanged(ConnectionState connectionState) {
            Log.i(TAG, "connectionStateChanged: "+ connectionState.name());
            if(connectionState == ConnectionState.ERROR){
                DeepstreamInstance.close();
            }
        }
    };

}
