package com.carecloud.carepay.patient.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 6/09/18.
 */
public class SurveyPayload {

    @Expose
    @SerializedName("survey")
    private SurveyModel survey = new SurveyModel();

    @Expose
    @SerializedName("surveys_settings")
    private SurveySettings surveySettings = new SurveySettings();

    public SurveyModel getSurvey() {
        return survey;
    }

    public void setSurvey(SurveyModel survey) {
        this.survey = survey;
    }

    public SurveySettings getSurveySettings() {
        return surveySettings;
    }

    public void setSurveySettings(SurveySettings surveySettings) {
        this.surveySettings = surveySettings;
    }
}
