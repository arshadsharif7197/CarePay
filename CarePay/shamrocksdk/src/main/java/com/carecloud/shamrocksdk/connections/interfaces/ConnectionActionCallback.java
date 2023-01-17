package com.carecloud.shamrocksdk.connections.interfaces;

import com.carecloud.shamrocksdk.connections.models.Device;
import com.google.gson.JsonElement;

/**
 * Callback for handling Payment Device updates via DeepStream
 */
public interface ConnectionActionCallback {

    /**
     * Called when an error is raised on the Device record in DeepStream. Clients should attempt to
     * reconnect to the device when an error is thrown
     * @param deviceName Device Record Name in DeepStream
     * @param errorCode DeepStream Error Code {@link io.deepstream.Event DeepStream Events}
     * @param errorMessage DeepStream Error Message String
     */
    void onConnectionError(String deviceName, String errorCode, String errorMessage);

    /**
     * Called when the Device Record is deleted in DeepStream. Clients should recreate the Device
     * @param deviceName Device Record Name in DeepStream
     */
    void onConnectionDestroyed(String deviceName);

    /**
     * Called when a change has been made to the Device Record in DeepStream
     * @param deviceName Device Record Name in DeepStream
     * @param device updated Device object
     */
    void onConnectionUpdate(String deviceName, Device device);

    /**
     * Called when an update has failed for a Device Record in DeepStream
     * @param deviceName Device Record Name in DeepStream
     * @param recordObject raw JSON record
     */
    void onConnectionUpdateFail(String deviceName, JsonElement recordObject);
}
