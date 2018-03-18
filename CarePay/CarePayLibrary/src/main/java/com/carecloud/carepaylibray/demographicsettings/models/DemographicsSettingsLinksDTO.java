
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsLinksDTO {

    @SerializedName("self")
    @Expose
    private DemographicsSettingsSelfDTO demographicsSettingsSelfDTO = new DemographicsSettingsSelfDTO();

    public DemographicsSettingsSelfDTO getSelf() {
        return demographicsSettingsSelfDTO;
    }

    public void setSelf(DemographicsSettingsSelfDTO demographicsSettingsSelfDTO) {
        this.demographicsSettingsSelfDTO = demographicsSettingsSelfDTO;
    }

}
