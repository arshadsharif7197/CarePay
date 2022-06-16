package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitTypeQuestions {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("options")
    @Expose
    private List<VisitTypeOptions> options;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<VisitTypeOptions> getOptions() {
        return options;
    }

    public void setOptions(List<VisitTypeOptions> options) {
        this.options = options;
    }
}
