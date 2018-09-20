package com.carecloud.carepay.patient.survey.model;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 6/09/18.
 */
public class SurveyDTO implements DTO {

    @Expose
    @SerializedName("payload")
    private SurveyPayload payload = new SurveyPayload();

    @Expose
    @SerializedName("metadata")
    private SurveyMetadata metadata = new SurveyMetadata();

    public SurveyPayload getPayload() {
        return payload;
    }

    public void setPayload(SurveyPayload payload) {
        this.payload = payload;
    }

    public SurveyMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SurveyMetadata metadata) {
        this.metadata = metadata;
    }
}
