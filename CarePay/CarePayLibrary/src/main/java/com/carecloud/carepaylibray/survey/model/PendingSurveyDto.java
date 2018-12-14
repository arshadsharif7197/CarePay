package com.carecloud.carepaylibray.survey.model;

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
    private PendingSurveyMetadata metadata = new PendingSurveyMetadata();

    public PendingSurveyPayload getPayload() {
        return payload;
    }

    public void setPayload(PendingSurveyPayload payload) {
        this.payload = payload;
    }

    public PendingSurveyMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingSurveyMetadata metadata) {
        this.metadata = metadata;
    }
}
