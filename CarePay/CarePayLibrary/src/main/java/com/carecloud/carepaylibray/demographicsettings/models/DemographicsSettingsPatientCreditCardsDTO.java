package com.carecloud.carepaylibray.demographicsettings.models;


import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicsInfoMetaDataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by harshal_patil on 1/5/2017.
 */
public class DemographicsSettingsPatientCreditCardsDTO {

    @SerializedName("metadata")
    @Expose
    private DemographicsInfoMetaDataDTO metadata = new DemographicsInfoMetaDataDTO();
    @SerializedName("payload")
    @Expose
    private List<DemographicsSettingsCreditCardsPayloadDTO> payload = new ArrayList<>();

    public DemographicsInfoMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(DemographicsInfoMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    public List<DemographicsSettingsCreditCardsPayloadDTO> getPayload() {
        return payload;
    }

    public void setPayload(List<DemographicsSettingsCreditCardsPayloadDTO> payload) {
        this.payload = payload;
    }

}

