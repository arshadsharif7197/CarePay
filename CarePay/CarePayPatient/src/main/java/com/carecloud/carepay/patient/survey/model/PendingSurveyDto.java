package com.carecloud.carepay.patient.survey.model;

import com.carecloud.carepay.patient.notifications.models.NotificationItemMetadata;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 13/09/18.
 */
public class PendingSurveyDto {

    @Expose
    @SerializedName("payload")
    private PendingSurveyPayload payload = new PendingSurveyPayload();

    @Expose
    @SerializedName("metadata")
    private NotificationItemMetadata metadata = new NotificationItemMetadata();

    public PendingSurveyPayload getPayload() {
        return payload;
    }

    public void setPayload(PendingSurveyPayload payload) {
        this.payload = payload;
    }

    public NotificationItemMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(NotificationItemMetadata metadata) {
        this.metadata = metadata;
    }
}
