package com.carecloud.carepay.service.library;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Service callback class allow client to update UI thread
 *
 */

public interface WorkflowServiceCallback {

    /**
     * Use for update UI before the background API call
     */
    void onPreExecute();

    /**
     * Use for update UI after the background API call
     * @param workflowDTO return API common dto and client have to have a conversion mechanism to
     *                    convert to the real object
     */
    void onPostExecute(WorkflowDTO workflowDTO);

    /**
     * Call API failure UI functionality
     * @param exceptionMessage return exception message. If needed client will customized
     *                         the default exception message
     */

    void onFailure(String exceptionMessage);
}
