
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsMetadataDTO {

    @SerializedName("links")
    @Expose
    private DemographicsSettingsLinksDTO demographicsSettingsLinksDTO = new DemographicsSettingsLinksDTO();
    @SerializedName("transitions")
    @Expose
    private DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = new DemographicsSettingsTransitionsDTO();

    public DemographicsSettingsLinksDTO getLinks() {
        return demographicsSettingsLinksDTO;
    }

    public void setLinks(DemographicsSettingsLinksDTO demographicsSettingsLinksDTO) {
        this.demographicsSettingsLinksDTO = demographicsSettingsLinksDTO;
    }

    public DemographicsSettingsTransitionsDTO getTransitions() {
        return demographicsSettingsTransitionsDTO;
    }

    public void setTransitions(DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO) {
        this.demographicsSettingsTransitionsDTO = demographicsSettingsTransitionsDTO;
    }


}
