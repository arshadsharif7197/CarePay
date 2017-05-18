package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 5/16/17.
 */

public class DemographicsField {

    @SerializedName("display")
    @Expose
    private boolean displayed = true;

    @SerializedName("required")
    @Expose
    private boolean required = false;

    @SerializedName("options")
    @Expose
    private List<DemographicsOption> options = new ArrayList<>();

    public boolean isDisplayed() {
        return displayed;
    }

    public void setDisplayed(boolean displayed) {
        this.displayed = displayed;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<DemographicsOption> getOptions() {
        return options;
    }

    public void setOptions(List<DemographicsOption> options) {
        this.options = options;
    }
}
