package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IntelligentSchedulerDTO {

    @SerializedName("enabled")
    @Expose
    private boolean enabled;
    @SerializedName("question")
    @Expose
    private VisitTypeQuestions question;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public VisitTypeQuestions getQuestion() {
        return question;
    }

    public void setQuestion(VisitTypeQuestions question) {
        this.question = question;
    }
}
