package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 * Model for payload info.
 */
public class DemographicPayloadInfoDTO {

    @SerializedName("metadata")
    @Expose
    private DemographicsInfoMetaDataDTO metadata = new DemographicsInfoMetaDataDTO();
    @SerializedName("payload")
    @Expose
    private DemographicPayloadDTO payload = new DemographicPayloadDTO();

    /**
     *
     * @return
     * The metadata
     */
    public DemographicsInfoMetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(DemographicsInfoMetaDataDTO metadata) {
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
