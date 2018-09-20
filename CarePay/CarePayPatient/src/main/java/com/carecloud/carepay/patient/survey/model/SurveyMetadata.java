package com.carecloud.carepay.patient.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 17/09/18.
 */
public class SurveyMetadata {

    @Expose
    @SerializedName("transitions")
    private SurveyTransitions transitions;

    public SurveyTransitions getTransitions() {
        return transitions;
    }

    public void setTransitions(SurveyTransitions transitions) {
        this.transitions = transitions;
    }
}
