package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepaylibray.demographics.dtos.payload.XDemographicsInfoMetaDataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/5/2017.
 */

public class DemographicsSettingsCreditCardsPayloadDTO {
    @SerializedName("metadata")
    @Expose
    private XDemographicsInfoMetaDataDTO metadata = new XDemographicsInfoMetaDataDTO();
    @SerializedName("payload")
    @Expose
    private DemographicsSettingsPayloadPropertiesDTO payload = new DemographicsSettingsPayloadPropertiesDTO();

    public XDemographicsInfoMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(XDemographicsInfoMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    public DemographicsSettingsPayloadPropertiesDTO getPayload() {
        return payload;
    }

    public void setPayload(DemographicsSettingsPayloadPropertiesDTO payload) {
        this.payload = payload;
    }
}
