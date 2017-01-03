
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsMetadataDTO {

    @SerializedName("labels")
    @Expose
    private DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO;
    @SerializedName("links")
    @Expose
    private DemographicsSettingsLinksDTO demographicsSettingsLinksDTO;
    @SerializedName("transitions")
    @Expose
    private DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO;

    public DemographicsSettingsLabelsDTO getLabels() {
        return demographicsSettingsLabelsDTO;
    }

    public void setLabels(DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO) {
        this.demographicsSettingsLabelsDTO = demographicsSettingsLabelsDTO;
    }

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
