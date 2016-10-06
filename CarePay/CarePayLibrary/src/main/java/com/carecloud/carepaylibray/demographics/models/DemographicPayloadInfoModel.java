package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/26/2016.
 */
public class DemographicPayloadInfoModel {

    @SerializedName("metadata")
    @Expose
    private DemographicPayloadInfoMetaDataModel metadata;
    @SerializedName("payload")
    @Expose
    private DemPayloadPojo payload;

    /**
     *
     * @return
     * The metadata
     */
    public DemographicPayloadInfoMetaDataModel getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(DemographicPayloadInfoMetaDataModel metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public DemPayloadPojo getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(DemPayloadPojo payload) {
        this.payload = payload;
    }
}
