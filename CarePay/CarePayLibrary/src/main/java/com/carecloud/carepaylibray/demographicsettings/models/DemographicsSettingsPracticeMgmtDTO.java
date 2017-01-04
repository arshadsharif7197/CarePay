
package com.carecloud.carepaylibray.demographicsettings.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPracticeMgmtDTO {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("validations")
    @Expose
    private List<DemographicsSettingsValidationDTO> demographicsSettingsValidationDTOs = null;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DemographicsSettingsValidationDTO> getValidations() {
        return demographicsSettingsValidationDTOs;
    }

    public void setValidations(List<DemographicsSettingsValidationDTO> demographicsSettingsValidationDTOs) {
        this.demographicsSettingsValidationDTOs = demographicsSettingsValidationDTOs;
    }

}
