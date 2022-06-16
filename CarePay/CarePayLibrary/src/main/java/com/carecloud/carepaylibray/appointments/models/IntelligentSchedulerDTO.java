package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Query String
 */
public class IntelligentSchedulerDTO {

    @SerializedName("enabled")
    @Expose
    private boolean enabled;
    @SerializedName("question")
    @Expose
    private List<VisitTypeQuestions> question;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<VisitTypeQuestions> getQuestion() {
        return question;
    }

    public void setQuestion(List<VisitTypeQuestions> question) {
        this.question = question;
    }
}
