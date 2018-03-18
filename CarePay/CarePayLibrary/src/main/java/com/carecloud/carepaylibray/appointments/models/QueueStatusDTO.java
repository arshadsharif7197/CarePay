package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jorge on 18/01/17.
 */

public class QueueStatusDTO {

    @SerializedName("payload")
    @Expose
    private QueueStatusInnerPayloadDTO queueStatusInnerPayload = new QueueStatusInnerPayloadDTO();

    public QueueStatusInnerPayloadDTO getQueueStatusInnerPayload() {
        return queueStatusInnerPayload;
    }

    public void setQueueStatusInnerPayload(QueueStatusInnerPayloadDTO queueStatusInnerPayload) {
        this.queueStatusInnerPayload = queueStatusInnerPayload;
    }
}
