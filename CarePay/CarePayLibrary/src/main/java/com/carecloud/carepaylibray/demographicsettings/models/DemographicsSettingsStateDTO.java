
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DemographicsSettingsStateDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<DemographicsSettingsValidationDTO> validations = new ArrayList<>();
    @SerializedName("options")
    @Expose
    private List<Object> options = new ArrayList<>();

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

    public List<DemographicsSettingsValidationDTO> getValidations() {
        return validations;
    }

    public void setValidations(List<DemographicsSettingsValidationDTO> validations) {
        this.validations = validations;
    }

    public List<Object> getOptions() {
        return options;
    }

    public void setOptions(List<Object> options) {
        this.options = options;
    }


}
