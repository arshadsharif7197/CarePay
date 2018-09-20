package com.carecloud.carepay.patient.survey.model;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 17/09/18.
 */
public class SurveyTransitions {

    @Expose
    @SerializedName("sendSurvey")
    private TransitionDTO sendSurvey = new TransitionDTO();

    public TransitionDTO getSendSurvey() {
        return sendSurvey;
    }

    public void setSendSurvey(TransitionDTO sendSurvey) {
        this.sendSurvey = sendSurvey;
    }
}
