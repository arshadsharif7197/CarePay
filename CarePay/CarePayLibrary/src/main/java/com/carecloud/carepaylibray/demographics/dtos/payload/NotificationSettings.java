package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 3/28/17.
 */

public class NotificationSettings {

    @SerializedName("metadata")
    @Expose
    private NotificationSettingsMetadata metadata = new NotificationSettingsMetadata();

    @SerializedName("payload")
    @Expose
    private NotificationSettingsPayload payload = new NotificationSettingsPayload();

    public NotificationSettingsMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(NotificationSettingsMetadata metadata) {
        this.metadata = metadata;
    }

    public NotificationSettingsPayload getPayload() {
        return payload;
    }

    public void setPayload(NotificationSettingsPayload payload) {
        this.payload = payload;
    }
}
