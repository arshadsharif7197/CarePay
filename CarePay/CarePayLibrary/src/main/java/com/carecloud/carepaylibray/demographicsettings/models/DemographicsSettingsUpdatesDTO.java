package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsUpdatesDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private DemographicsSettingsPropertiesDTO properties = new DemographicsSettingsPropertiesDTO();
    @SerializedName("method")
    @Expose
    private DemographicsSettingsMethodDTO method = new DemographicsSettingsMethodDTO();
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

    public DemographicsSettingsPropertiesDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicsSettingsPropertiesDTO properties) {
        this.properties = properties;
    }

    public DemographicsSettingsMethodDTO getMethod() {
        return method;
    }

    public void setMethod(DemographicsSettingsMethodDTO method) {
        this.method = method;
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

