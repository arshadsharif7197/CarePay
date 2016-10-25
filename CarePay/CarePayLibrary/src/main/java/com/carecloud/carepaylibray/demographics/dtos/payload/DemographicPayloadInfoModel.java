package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 * Model for payload info.
 */
public class DemographicPayloadInfoModel {

    @SerializedName("metadata")
    @Expose
    private DemographicPayloadInfoMetaDataDTO metadata;
    @SerializedName("payload")
    @Expose
    private DemographicPayloadDTO             payload;

    /**
     *
     * @return
     * The metadata
     */
    public DemographicPayloadInfoMetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(DemographicPayloadInfoMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public DemographicPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(DemographicPayloadDTO payload) {
        this.payload = payload;
    }
}
