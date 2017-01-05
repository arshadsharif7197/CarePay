package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */

public class DemographicsSettingsAddressDTO {
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("method")
    @Expose
    private DemographicsSettingsMethodDTO method;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private DemographicsSettingsPropertiesDTO properties;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public DemographicsSettingsMethodDTO getMethod() {
        return method;
    }

    public void setMethod(DemographicsSettingsMethodDTO method) {
        this.method = method;
    }

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

}
