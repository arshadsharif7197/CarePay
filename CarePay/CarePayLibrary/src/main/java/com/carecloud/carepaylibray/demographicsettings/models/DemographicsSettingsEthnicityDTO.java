package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshal_patil on 1/6/2017.
 */

public class DemographicsSettingsEthnicityDTO {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("options")
    @Expose
    private List<DemographicsSettingsOptionDTO> options = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<DemographicsSettingsOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<DemographicsSettingsOptionDTO> options) {
        this.options = options;
    }

}
