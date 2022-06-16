package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Query String
 */
public class VisitTypeOptions {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("visitType")
    @Expose
    private String visitType;
    @SerializedName("question")
    @Expose
    private List<VisitTypeQuestions> question;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVisitType() {
        return visitType;
    }

    public void setVisitType(String visitType) {
        this.visitType = visitType;
    }

    public List<VisitTypeQuestions> getQuestion() {
        return question;
    }

    public void setQuestion(List<VisitTypeQuestions> question) {
        this.question = question;
    }
}
