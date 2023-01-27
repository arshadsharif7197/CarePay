package com.carecloud.shamrocksdk.registrations.interfaces;

import com.carecloud.shamrocksdk.registrations.models.Registration;

/**
 * Handles registering Clover Device with Shamrock Payments
 */

public interface RegistrationCallback {

     /**
      * Called prior to making registering device with Shamrock Payments
      */
     void onPreExecute();

     /**
      * Called upon successful refistration of Clover device with Shamrock Payments
      * @param registration JSON Response
      */
     void onPostExecute(Registration registration);

     /**
      * Called if exception or error occurs while registering Clover device with Shamrock Payments
      * @param errorMessage Error Message
      */
     void onFailure(String errorMessage);
}
