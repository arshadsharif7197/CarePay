package com.carecloud.carepaylibray.practice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by kkannan on 5/2/17.
 */

public class CheckInWorkFlowDTO {
    @SerializedName("payload")
    @Expose
    private CheckInWorkflowPayloadDTO payload = new CheckInWorkflowPayloadDTO();

    /**
     *
     * @return
     *     The payload
     */
    public CheckInWorkflowPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     *     The payload
     */
    public void setPayload(CheckInWorkflowPayloadDTO payload) {
        this.payload = payload;
    }
}
