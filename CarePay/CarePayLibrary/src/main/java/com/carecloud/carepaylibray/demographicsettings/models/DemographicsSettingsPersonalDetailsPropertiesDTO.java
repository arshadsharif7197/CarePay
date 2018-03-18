package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by harshal_patil on 1/6/2017.
 */
public class DemographicsSettingsPersonalDetailsPropertiesDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("validations")
    @Expose
    private List<DemographicsSettingsValidationDTO> validations = new ArrayList<>();
    @SerializedName("method")
    @Expose
    private DemographicsSettingsMethodDTO method = new DemographicsSettingsMethodDTO();
    @SerializedName("properties")
    @Expose
    private DemographicsSettingsPersonalDetailsDTO properties = new DemographicsSettingsPersonalDetailsDTO();

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

    public DemographicsSettingsMethodDTO getMethod() {
        return method;
    }

    public void setMethod(DemographicsSettingsMethodDTO method) {
        this.method = method;
    }

    public DemographicsSettingsPersonalDetailsDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicsSettingsPersonalDetailsDTO properties) {
        this.properties = properties;
    }

}
