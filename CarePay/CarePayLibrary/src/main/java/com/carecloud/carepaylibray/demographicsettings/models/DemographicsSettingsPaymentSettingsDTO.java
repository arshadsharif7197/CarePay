
package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPaymentSettingsDTO {

    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsMetadataDTO metadata = new DemographicsSettingsMetadataDTO();
    @SerializedName("payload")
    @Expose
    private DemographicsSettingsPayloadDTO payload = new DemographicsSettingsPayloadDTO();

    public DemographicsSettingsMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(DemographicsSettingsMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public DemographicsSettingsPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(DemographicsSettingsPayloadDTO payload) {
        this.payload = payload;
    }

}
