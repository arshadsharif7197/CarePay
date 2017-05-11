package com.carecloud.carepay.patient.notifications.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationsDTO implements DTO {

    @SerializedName("metadata")
    @Expose
    private NotificationsMetadataDTO metadata = new NotificationsMetadataDTO();

    @SerializedName("payload")
    @Expose
    private NotificationsPayload payload = new NotificationsPayload();

    @SerializedName("state")
    @Expose
    private String state;

    public NotificationsMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(NotificationsMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public NotificationsPayload getPayload() {
        return payload;
    }

    public void setPayload(NotificationsPayload payload) {
        this.payload = payload;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
