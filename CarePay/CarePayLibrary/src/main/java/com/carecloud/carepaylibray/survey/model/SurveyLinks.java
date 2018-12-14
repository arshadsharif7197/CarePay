package com.carecloud.carepaylibray.survey.model;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 9/10/18.
 */
public class SurveyLinks {

    @Expose
    @SerializedName("get_survey")
    private TransitionDTO getSurvey;

    @Expose
    @SerializedName(value = "language", alternate = "language_metadata")
    private TransitionDTO language;

    public TransitionDTO getGetSurvey() {
        return getSurvey;
    }

    public void setGetSurvey(TransitionDTO getSurvey) {
        this.getSurvey = getSurvey;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }

    public TransitionDTO getLanguage() {
        return language;
    }
}
