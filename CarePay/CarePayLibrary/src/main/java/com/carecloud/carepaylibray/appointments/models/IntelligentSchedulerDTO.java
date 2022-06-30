package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IntelligentSchedulerDTO {

    @SerializedName("isSchedulerEnabled")
    @Expose
    private boolean isSchedulerEnabled;
    @SerializedName("intelligent_scheduler_questions")
    @Expose
    private List<VisitTypeQuestions> intelligent_scheduler_questions;

    public boolean isSchedulerEnabled() {
        return isSchedulerEnabled;
    }

    public void setSchedulerEnabled(boolean schedulerEnabled) {
        this.isSchedulerEnabled = schedulerEnabled;
    }

    public List<VisitTypeQuestions> getIntelligent_scheduler_questions() {
        return intelligent_scheduler_questions;
    }

    public void setIntelligent_scheduler_questions(List<VisitTypeQuestions> intelligent_scheduler_questions) {
        this.intelligent_scheduler_questions = intelligent_scheduler_questions;
    }
}
