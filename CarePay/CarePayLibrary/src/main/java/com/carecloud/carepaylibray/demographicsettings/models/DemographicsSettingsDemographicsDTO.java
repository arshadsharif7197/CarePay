package com.carecloud.carepaylibray.demographicsettings.models;

/**
 * Created by harshal_patil on 1/5/2017.
 */

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.XDemographicsInfoMetaDataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsDemographicsDTO {

    @SerializedName("metadata")
    @Expose
    private XDemographicsInfoMetaDataDTO metadata = new XDemographicsInfoMetaDataDTO();
    @SerializedName("payload")
    @Expose
    private DemographicPayloadDTO payload = new DemographicPayloadDTO();

    public XDemographicsInfoMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(XDemographicsInfoMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    public DemographicPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(DemographicPayloadDTO payload) {
        this.payload = payload;
    }

}

