package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */

public class DemographicsSettingsCreditCardsPayloadDTO {
    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsCreditCardMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private DemographicsSettingsPayloadPropertiesDTO payload;

    public DemographicsSettingsCreditCardMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(DemographicsSettingsCreditCardMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public DemographicsSettingsPayloadPropertiesDTO getPayload() {
        return payload;
    }

    public void setPayload(DemographicsSettingsPayloadPropertiesDTO payload) {
        this.payload = payload;
    }
}