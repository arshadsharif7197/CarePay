
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsBillingInformationDTO {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("properties")
    @Expose
    private DemographicsSettingsBillingInfoPropertiesDTO properties = new DemographicsSettingsBillingInfoPropertiesDTO();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DemographicsSettingsBillingInfoPropertiesDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicsSettingsBillingInfoPropertiesDTO properties) {
        this.properties = properties;
    }

}
