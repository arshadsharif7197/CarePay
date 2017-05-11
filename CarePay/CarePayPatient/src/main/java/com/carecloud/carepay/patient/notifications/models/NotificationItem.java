package com.carecloud.carepay.patient.notifications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationItem {

    @SerializedName("metadata")
    @Expose
    private NotificationItemMetadata metadata = new NotificationItemMetadata();

    @SerializedName("payload")
    @Expose
    private NotificationItemPayload payload = new NotificationItemPayload();

    public NotificationItemMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(NotificationItemMetadata metadata) {
        this.metadata = metadata;
    }

    public NotificationItemPayload getPayload() {
        return payload;
    }

    public void setPayload(NotificationItemPayload payload) {
        this.payload = payload;
    }
}
