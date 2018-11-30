package com.carecloud.carepaylibray.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 17/09/18.
 */
public class SurveyMetadata {

    @Expose
    @SerializedName("transitions")
    private SurveyTransitions transitions;

    @Expose
    @SerializedName("links")
    private SurveyLinks links;

    public SurveyTransitions getTransitions() {
        return transitions;
    }

    public void setTransitions(SurveyTransitions transitions) {
        this.transitions = transitions;
    }

    public SurveyLinks getLinks() {
        return links;
    }

    public void setLinks(SurveyLinks links) {
        this.links = links;
    }
}
