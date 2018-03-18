package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 19/01/17.
 */

public class QueueStatusInnerPayloadDTO {
    @SerializedName("queue_list")
    @Expose
    private List<QueueDTO> queueList = new ArrayList<>();

    public List<QueueDTO> getQueueList() {
        return queueList;
    }

    public void setQueueList(List<QueueDTO> queueList) {
        this.queueList = queueList;
    }
}
