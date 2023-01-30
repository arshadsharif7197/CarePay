package com.carecloud.shamrocksdk.connections.interfaces;



import androidx.annotation.Nullable;

import com.carecloud.shamrocksdk.connections.models.Device;

/**
 * Callback for handling Payment Device connectivity to Shamrock Payments
 */

public interface ConnectionCallback {

     /**
      * Called prior to connecting Device to DeepStream
      */
     void startDeviceConnection();

     /**
      * Called upon successful connection of Device to DeepStream
      * @param device Device object
      */
     void onDeviceConnected(Device device);

     /**
      * Called if exception occurs
      * @param errorMessage Error Message
      */
     void onConnectionFailure(String errorMessage);

     /**
      * Called upon successful disconnect of Device from DeepStream
      * @param device Device object can be null if error occurs when disconnecting
      */
     void onDeviceDisconnected(@Nullable Device device);
}
