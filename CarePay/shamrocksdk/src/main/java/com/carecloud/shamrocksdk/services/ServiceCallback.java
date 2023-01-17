package com.carecloud.shamrocksdk.services;

import com.google.gson.JsonElement;

/**
 * Service callback class allow client to update UI thread
 *
 */

public interface ServiceCallback {

    /**
     * Use for update UI before the background API call
     */
    void onPreExecute();

    /**
     * Use for update UI after the background API call
     * @param jsonElement return API common dto and client have to have a conversion mechanism to
     *                    convert to the real object
     */
    void onPostExecute(JsonElement jsonElement);

    /**
     * Call API failure UI functionality
     * @param exceptionMessage return exception message. If needed client will customized
     *                         the default exception message
     */

    void onFailure(String exceptionMessage);
}
