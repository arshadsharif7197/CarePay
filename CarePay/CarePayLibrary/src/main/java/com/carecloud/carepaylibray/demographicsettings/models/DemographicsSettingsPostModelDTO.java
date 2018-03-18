
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPostModelDTO {

    @SerializedName("$schema")
    @Expose
    private String $schema;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private DemographicsSettingsPropertiesDTO demographicsSettingsPropertiesDTO = new DemographicsSettingsPropertiesDTO();

    public String get$schema() {
        return $schema;
    }

    public void set$schema(String $schema) {
        this.$schema = $schema;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DemographicsSettingsPropertiesDTO getProperties() {
        return demographicsSettingsPropertiesDTO;
    }

    public void setProperties(DemographicsSettingsPropertiesDTO demographicsSettingsPropertiesDTO) {
        this.demographicsSettingsPropertiesDTO = demographicsSettingsPropertiesDTO;
    }

}
