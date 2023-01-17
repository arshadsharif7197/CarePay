package com.carecloud.shamrocksdk.connections;


import android.util.Log;

import androidx.annotation.NonNull;

import com.carecloud.shamrocksdk.connections.interfaces.ConnectionActionCallback;
import com.carecloud.shamrocksdk.connections.interfaces.ListDeviceCallback;
import com.carecloud.shamrocksdk.connections.models.Device;
import com.carecloud.shamrocksdk.constants.HttpConstants;
import com.carecloud.shamrocksdk.utils.DeepstreamInstance;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import static com.carecloud.shamrocksdk.utils.AuthorizationUtil.getAuthorization;

import java.util.ArrayList;
import java.util.List;

import io.deepstream.ConnectionState;
import io.deepstream.ConnectionStateListener;
import io.deepstream.DeepstreamClient;
import io.deepstream.DeepstreamRuntimeErrorHandler;
import io.deepstream.Event;
import io.deepstream.ListChangedListener;
import io.deepstream.LoginResult;
import io.deepstream.Record;
import io.deepstream.RecordChangedCallback;
import io.deepstream.RecordEventsListener;
import io.deepstream.Topic;

/**
 * Handles listing devices as well as updating and subscribing to lists and device changes
 */

public class DeviceInfo {
    private static final String TAG = DeviceInfo.class.getName();
    private static final String GROUP_PATH_PREFIX = "device_group/";
    private static final String DEVICE_PATH_PREFIX = "device/";

    private static ConnectionActionCallback currentActionCallback;
    private static ListDeviceCallback currentListDeviceCallback;
    private static List<Record> subscribedDevices = new ArrayList<>();

    /**
     * List all available devices for a device group. This method will also subscribe to changed in
     * both the group list and the individual devices in the list. This method requires authentication
     * with ShamrockPayments. This will subscribe to both the list as well as all devices in the list
     *
     * @param userId Shamrock Payments userId
     * @param authToken Shamrock Payments auth token
     * @param groupName name of group to get list of devices
     * @param deviceCallback callback to handle connection and retrieval of device list
     * @param connectionActionCallback callback to handle updates to devices in the list
     */
    public static void listDevices(String userId, String authToken, String groupName, @NonNull final ListDeviceCallback deviceCallback, @NonNull ConnectionActionCallback connectionActionCallback){
        currentListDeviceCallback = deviceCallback;
        deviceCallback.onPreExecute();

        try {
            DeepstreamInstance.close();
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            client.setRuntimeErrorHandler(new DeepstreamRuntimeErrorHandler() {
                @Override
                public void onException(Topic topic, Event event, String errorMessage) {
                    deviceCallback.onFailure(errorMessage);
                    Log.d(TAG, event.toString() + ": " + errorMessage);
                }
            });

            client.addConnectionChangeListener(new ConnectionStateListener() {
                @Override
                public void connectionStateChanged(ConnectionState connectionState) {
                    Log.d(TAG, "connectionStateChanged: "+ connectionState.name());

                }
            }) ;

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if(loginResult == null){
                    deviceCallback.onFailure("Unable to login client");
                    return;
                }
                if(!loginResult.loggedIn()){
                    deviceCallback.onFailure(loginResult.getErrorEvent().name());
                    return;
                }
            }

            if(!subscribedDevices.isEmpty()){
                for(Record deviceRecord : subscribedDevices){
                    DeepstreamInstance.unSubscribeRecord(deviceRecord, recordChangedCallback, null);
                }
            }
            subscribedDevices.clear();

            io.deepstream.List deviceList = client.record.getList(GROUP_PATH_PREFIX + groupName);
            if(deviceList != null){
                currentActionCallback = connectionActionCallback;

                DeepstreamInstance.subscribeList(deviceList, listChangedListener, recordEventsListener);

                Gson gson = new Gson();
                List<Device> devices = new ArrayList<>();
                for(String entry : deviceList.getEntries()){
                    Record deviceRecord = client.record.getRecord(entry);

                    DeepstreamInstance.subscribeRecord(deviceRecord, recordChangedCallback, null);

                    devices.add(gson.fromJson(deviceRecord.get(), Device.class));

                    subscribedDevices.add(deviceRecord);
                }

                deviceCallback.onPostExecute(devices);
            }else{
                deviceCallback.onFailure("Unable to retrieve Device Record");
            }

        } catch (Exception e) {
            deviceCallback.onFailure("Device Failed, Please retry");
            e.printStackTrace();
        }

    }

    /**
     * Update a device in DeepStream. This call requires authentication with Shamrock Payments. This
     * will allow clients to post a payment request to a device
     *
     * @param userId Shamrock Payments userId
     * @param authToken Shamrock Payments auth token
     * @param deviceId id of device to update
     * @param device updated device model to set in DeepStream
     */
    public static void updateDevice(String userId, String authToken, String deviceId, Device device){

        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            client.addConnectionChangeListener(new ConnectionStateListener() {
                @Override
                public void connectionStateChanged(ConnectionState connectionState) {
                    Log.d(TAG, "connectionStateChanged: " + connectionState.name());

                }
            });


            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if(loginResult == null || !loginResult.loggedIn()){
                    return;
                }
            }

            Record record = client.record.getRecord(DEVICE_PATH_PREFIX + deviceId);
            if(record != null){
                Gson gson = new Gson();
                record.set(gson.toJsonTree(device));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Remove all active subscriptions to lists and devices. This call requires authentication with
     * Shamrock Payments
     *
     * @param userId Shamrock Payments userId
     * @param authToken Shamrock Payments auth token
     */
    public static void releaseAllDevices(String userId, String authToken){
        Log.d(TAG, "Start release devices");

        try {
            DeepstreamClient client = DeepstreamInstance.getClient(HttpConstants.getDeepstreamUrl());

            ConnectionState connectionState = client.getConnectionState();
            if(connectionState != ConnectionState.OPEN){
                LoginResult loginResult = client.login(getAuthorization(userId, authToken));
                if(loginResult == null || !loginResult.loggedIn()){
                    return;
                }
            }

            if(!subscribedDevices.isEmpty()){
                for(Record deviceRecord : subscribedDevices){
                    DeepstreamInstance.unSubscribeRecord(deviceRecord, recordChangedCallback, null);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private static ListChangedListener listChangedListener = new ListChangedListener() {
        @Override
        public void onListChanged(String string, String[] deviceIds) {
            currentListDeviceCallback.onListChanged();
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

}
