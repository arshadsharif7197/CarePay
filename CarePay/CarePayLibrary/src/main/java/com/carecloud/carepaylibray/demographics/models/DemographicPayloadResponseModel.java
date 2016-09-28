package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 */

public class DemographicPayloadResponseModel {
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoModel demographics;


    public void setDemographics(DemographicPayloadInfoModel demographics) {
        this.demographics = demographics;
    }

    public DemographicPayloadInfoModel getDemographics() {
        return demographics;
    }
}
