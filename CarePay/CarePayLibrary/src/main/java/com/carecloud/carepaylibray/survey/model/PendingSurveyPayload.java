package com.carecloud.carepaylibray.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 13/09/18.
 */
public class PendingSurveyPayload {

    @Expose
    @SerializedName("survey_uuid")
    private String surveyUuid;
    @Expose
    @SerializedName("survey_version")
    private String surveyVersion;
    @Expose
    @SerializedName("survey_type")
    private String surveyType;

    public String getSurveyUuid() {
        return surveyUuid;
    }

    public void setSurveyUuid(String surveyUuid) {
        this.surveyUuid = surveyUuid;
    }

    public String getSurveyVersion() {
        return surveyVersion;
    }

    public void setSurveyVersion(String surveyVersion) {
        this.surveyVersion = surveyVersion;
    }

    public String getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(String surveyType) {
        this.surveyType = surveyType;
    }
}
