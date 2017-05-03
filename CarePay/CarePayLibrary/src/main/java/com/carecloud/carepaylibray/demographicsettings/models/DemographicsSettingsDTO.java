
package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsDTO implements DTO{

    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO = new DemographicsSettingsMetadataDTO();
    @SerializedName("payload")
    @Expose
    private DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO = new DemographicsSettingsPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    public DemographicsSettingsMetadataDTO getDemographicsSettingsMetadataDTO() {
        return demographicsSettingsMetadataDTO;
    }

    public void setDemographicsSettingsMetadataDTO(DemographicsSettingsMetadataDTO demographicsSettingsMetadataDTO) {
        this.demographicsSettingsMetadataDTO = demographicsSettingsMetadataDTO;
    }

    public DemographicsSettingsPayloadDTO getPayload() {
        return demographicsSettingsPayloadDTO;
    }

    public void setPayload(DemographicsSettingsPayloadDTO demographicsSettingsPayloadDTO) {
        this.demographicsSettingsPayloadDTO = demographicsSettingsPayloadDTO;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
