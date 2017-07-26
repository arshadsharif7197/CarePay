package com.carecloud.carepay.mini.services.carepay;

import com.google.gson.JsonElement;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Service callback class allow client to update UI thread
 *
 */

public interface RestCallServiceCallback {

    /**
     * Called prior to making REST Service Call
     */
    void onPreExecute();

    /**
     * Called upon successful completion of REST Service Call
     * @param jsonElement JSON Response
     */
    void onPostExecute(JsonElement jsonElement);

    /**
     * Called if exception occurs or if call returns an error
     * @param errorMessage Pre-Processed Error Message
     */
    void onFailure(String errorMessage);
}
