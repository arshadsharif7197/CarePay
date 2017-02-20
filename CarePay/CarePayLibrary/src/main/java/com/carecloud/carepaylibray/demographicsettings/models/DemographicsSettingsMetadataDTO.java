
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsMetadataDTO {

    @SerializedName("labels")
    @Expose
    private DemographicsSettingsLabelsDTO demographicsSettingsLabelsDTO = new DemographicsSettingsLabelsDTO();
    @SerializedName("links")
    @Expose
    private DemographicsSettingsLinksDTO demographicsSettingsLinksDTO = new DemographicsSettingsLinksDTO();
    @SerializedName("transitions")
    @Expose
    private DemographicsSettingsTransitionsDTO demographicsSettingsTransitionsDTO = new DemographicsSettingsTransitionsDTO();
    @SerializedName("data_models")
    @Expose
    private DemographicsSettingsDataModelsDTO dataModels = new DemographicsSettingsDataModelsDTO();

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

    public DemographicsSettingsDataModelsDTO getDataModels() {
        return dataModels;
    }

    public void setDataModels(DemographicsSettingsDataModelsDTO dataModels) {
        this.dataModels = dataModels;
    }

}
