package com.carecloud.shamrocksdk.connections.interfaces;

import com.carecloud.shamrocksdk.connections.models.Device;

import java.util.List;

/**
 * Callback for connecting to DeepStream to view available Devices
 */

public interface ListDeviceCallback {

    /**
     * Called Prior to connecting to DeepStream
     */
    void onPreExecute();

    /**
     * Called when list of Available Devices is ready
     * @param devices List of Available Devices
     */
    void onPostExecute(List<Device> devices);

    /**
     * Called when an Error Occurs while retrieving the list of Devices
     * @param errorMessage Error message
     */
    void onFailure(String errorMessage);

    /**
     * Callback whenever a Device List Changes. Clients should retrieve an updated list of Devices
     */
    void onListChanged();

}
