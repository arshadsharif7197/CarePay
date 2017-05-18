package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/16/17.
 */

public class DemographicsOption {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("label")
    @Expose
    private String label;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
