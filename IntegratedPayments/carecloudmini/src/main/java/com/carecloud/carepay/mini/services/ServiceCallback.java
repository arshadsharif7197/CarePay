package com.carecloud.carepay.mini.services;

/**
 * Created by kkannan on 05/25/2017
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
     * @param serviceResponseDTO return API common dto and client have to have a conversion mechanism to
     *                    convert to the real object
     */
    void onPostExecute(ServiceResponseDTO serviceResponseDTO);

    /**
     * Call API failure UI functionality
     * @param exceptionMessage return exception message. If needed client will customized
     *                         the default exception message
     */

    void onFailure(String exceptionMessage);
}
