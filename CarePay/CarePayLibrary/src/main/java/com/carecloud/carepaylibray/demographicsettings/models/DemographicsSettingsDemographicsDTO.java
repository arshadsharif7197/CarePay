package com.carecloud.carepaylibray.demographicsettings.models;

/**
 * Created by harshal_patil on 1/5/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsDemographicsDTO {

    @SerializedName("metadata")
    @Expose
    private DemographicsSettingsCreditCardMetadataDTO metadata = new DemographicsSettingsCreditCardMetadataDTO();
    @SerializedName("payload")
    @Expose
    private DemographicsSettingsDemographicPayloadDTO payload = new DemographicsSettingsDemographicPayloadDTO();

    public DemographicsSettingsCreditCardMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(DemographicsSettingsCreditCardMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public DemographicsSettingsDemographicPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(DemographicsSettingsDemographicPayloadDTO payload) {
        this.payload = payload;
    }

}

