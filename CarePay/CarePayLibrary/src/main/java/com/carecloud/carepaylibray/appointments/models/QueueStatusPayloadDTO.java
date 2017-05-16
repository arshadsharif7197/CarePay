package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jorge on 18/01/17.
 */

public class QueueStatusPayloadDTO {
    @SerializedName("queue_status")
    @Expose
    private QueueStatusDTO queueStatus = new QueueStatusDTO();

    public QueueStatusDTO getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusDTO queueStatus) {
        this.queueStatus = queueStatus;
    }
}
