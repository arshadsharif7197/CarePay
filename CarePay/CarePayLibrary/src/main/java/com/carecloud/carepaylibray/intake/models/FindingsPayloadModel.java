
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class FindingsPayloadModel {

    @SerializedName("payload")
    @Expose
    private PayloadFindingsModel payload = new PayloadFindingsModel();
    @SerializedName("metadata")
    @Expose
    private MetadataFindingsModel metadata = new MetadataFindingsModel();

    /**
     * 
     * @return
     *     The payload
     */
    public PayloadFindingsModel getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(PayloadFindingsModel payload) {
        this.payload = payload;
    }

    /**
     * 
     * @return
     *     The metadata
     */
    public MetadataFindingsModel getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(MetadataFindingsModel metadata) {
        this.metadata = metadata;
    }

}
