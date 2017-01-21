package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by jorge on 18/01/17.
 */

public class QueueStatusPayloadDTO {
    @SerializedName("queue_status")
    @Expose
    private QueueStatusDTO queueStatus;

    public QueueStatusDTO getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(QueueStatusDTO queueStatus) {
        this.queueStatus = queueStatus;
    }
}
