package com.carecloud.carepaylibray.demographics.models;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicModel {
    @SerializedName("metadata")
    @Expose
    private DemographicMetadataModel metadata;
    @SerializedName("payload")
    @Expose
    private DemographicPayloadModel payload;

    /**
     *
     * @return
     * The metadata
     */
    public DemographicMetadataModel getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(DemographicMetadataModel metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public DemographicPayloadModel getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(DemographicPayloadModel payload) {
        this.payload = payload;
    }
}
