package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IntelligentSchedulerDTO {

    @SerializedName("isSchedulerEnabled")
    @Expose
    private boolean isSchedulerEnabled;

    @SerializedName("isEstablishedpatientschedulerenabled")
    @Expose
    private boolean isEstablishedPatientSchedulerEnabled;

    @SerializedName("intelligent_scheduler_questions")
    @Expose
    private List<VisitTypeQuestions> intelligent_scheduler_questions;

    @SerializedName("established_patient_intelligent_scheduler_questions")
    @Expose
    private List<VisitTypeQuestions> establishedPatientIntelligentSchedulerQuestions;

    @SerializedName("practice_id")
    @Expose
    private String practice_id;
    @SerializedName("practice_mgmt")
    @Expose
    private String practice_mgmt;


    public boolean isSchedulerEnabled() {
        return isSchedulerEnabled;
    }

    public boolean isEstablishedPatientSchedulerEnabled() {
        return isEstablishedPatientSchedulerEnabled;
    }

    public void setEstablishedPatientSchedulerEnabled(boolean establishedPatientSchedulerEnabled) {
        isEstablishedPatientSchedulerEnabled = establishedPatientSchedulerEnabled;
    }

    public List<VisitTypeQuestions> getEstablishedPatientIntelligentSchedulerQuestions() {
        return establishedPatientIntelligentSchedulerQuestions;
    }

    public void setEstablishedPatientIntelligentSchedulerQuestions(List<VisitTypeQuestions> establishedPatientIntelligentSchedulerQuestions) {
        this.establishedPatientIntelligentSchedulerQuestions = establishedPatientIntelligentSchedulerQuestions;
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

    public String getPractice_id() {
        return practice_id;
    }

    public void setPractice_id(String practice_id) {
        this.practice_id = practice_id;
    }

    public String getPractice_mgmt() {
        return practice_mgmt;
    }

    public void setPractice_mgmt(String practice_mgmt) {
        this.practice_mgmt = practice_mgmt;
    }
}
