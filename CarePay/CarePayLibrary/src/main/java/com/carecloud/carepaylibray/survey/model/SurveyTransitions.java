package com.carecloud.carepaylibray.survey.model;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 17/09/18.
 */
public class SurveyTransitions {

    @Expose
    @SerializedName("save_survey_response")
    private TransitionDTO saveSurvey = new TransitionDTO();
    @Expose
    @SerializedName("continue")
    private TransitionDTO continueTransition = new TransitionDTO();

    public TransitionDTO getSaveSurvey() {
        return saveSurvey;
    }

    public void setSaveSurvey(TransitionDTO saveSurvey) {
        this.saveSurvey = saveSurvey;
    }

    public TransitionDTO getContinueTransition() {
        return continueTransition;
    }

    public void setContinueTransition(TransitionDTO continueTransition) {
        this.continueTransition = continueTransition;
    }
}
