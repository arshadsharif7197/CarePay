package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicPayloadModel {

    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoModel demographics;

    /**
     *
     * @return
     * The demographics
     */
    public DemographicPayloadInfoModel getDemographics() {
        return demographics;
    }

    /**
     *
     * @param demographics
     * The demographics
     */
    public void setDemographics(DemographicPayloadInfoModel demographics) {
        this.demographics = demographics;
    }
}
