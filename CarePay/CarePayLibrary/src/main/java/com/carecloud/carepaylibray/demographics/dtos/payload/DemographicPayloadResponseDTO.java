package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 * Model for payload of response
 */
public class DemographicPayloadResponseDTO {
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoModel demographics;

    public DemographicPayloadInfoModel getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicPayloadInfoModel demographics) {
        this.demographics = demographics;
    }

}
