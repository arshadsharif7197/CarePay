package com.carecloud.carepaylibray.demographicsettings.models;

/**
 * Created by harshal_patil on 1/5/2017.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsIdentityDocumentsDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("method")
    @Expose
    private DemographicsSettingsMethodDTO method = new DemographicsSettingsMethodDTO();
    @SerializedName("properties")
    @Expose
    private DemographicsSettingsPropertiesDTO properties;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DemographicsSettingsMethodDTO getMethod() {
        return method;
    }

    public void setMethod(DemographicsSettingsMethodDTO method) {
        this.method = method;
    }

    public DemographicsSettingsPropertiesDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicsSettingsPropertiesDTO properties) {
        this.properties = properties;
    }

}
