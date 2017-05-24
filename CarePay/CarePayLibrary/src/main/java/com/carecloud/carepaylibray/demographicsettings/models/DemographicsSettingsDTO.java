
package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.DemographicMetadataDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadResponseDTO;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsDTO implements DTO{

    @SerializedName("metadata")
    @Expose
    private DemographicMetadataDTO metadata = new DemographicMetadataDTO();
    @SerializedName("payload")
    @Expose
    private DemographicPayloadResponseDTO payload = new DemographicPayloadResponseDTO();
    @SerializedName("state")
    @Expose
    private String state;

    public DemographicMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(DemographicMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public DemographicPayloadResponseDTO getPayload() {
        return payload;
    }

    public void setPayload(DemographicPayloadResponseDTO demographicsSettingsPayloadDTO) {
        this.payload = demographicsSettingsPayloadDTO;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
