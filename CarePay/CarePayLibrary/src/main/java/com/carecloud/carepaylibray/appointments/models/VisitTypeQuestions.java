package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitTypeQuestions {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("visittype")
    @Expose
    private VisitTypeDTO visittype;

    @SerializedName("childrens")
    @Expose
    private List<VisitTypeQuestions> childrens;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VisitTypeDTO getVisittype() {
        return visittype;
    }

    public void setVisittype(VisitTypeDTO visittype) {
        this.visittype = visittype;
    }

    public List<VisitTypeQuestions> getChildrens() {
        return childrens;
    }

    public void setChildrens(List<VisitTypeQuestions> childrens) {
        this.childrens = childrens;
    }
}
