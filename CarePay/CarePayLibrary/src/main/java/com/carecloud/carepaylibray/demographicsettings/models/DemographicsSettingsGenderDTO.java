package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by harshal_patil on 1/6/2017.
 */

public class DemographicsSettingsGenderDTO {
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
   /* @SerializedName("validations")
    @Expose
    private List<DemographicsSettingsValidationDTO> validations = null;*/
    @SerializedName("options")
    @Expose
    private List<DemographicsSettingsOptionDTO> options = null;

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

 /*   public List<DemographicsSettingsValidationDTO> getValidations() {
        return validations;
    }

    public void setValidations(List<DemographicsSettingsValidationDTO> validations) {
        this.validations = validations;
    }*/

    public List<DemographicsSettingsOptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<DemographicsSettingsOptionDTO> options) {
        this.options = options;
    }
}
