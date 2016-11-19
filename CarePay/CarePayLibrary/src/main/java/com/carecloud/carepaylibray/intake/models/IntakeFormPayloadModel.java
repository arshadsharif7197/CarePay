
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class IntakeFormPayloadModel {

    @SerializedName("metadata")
    @Expose
    private MetadataIntakeFormModel metadata;
    @SerializedName("payload")
    @Expose
    private PayloadIntakeFormModel payload;

    /**
     * 
     * @return
     *     The metadata
     */
    public MetadataIntakeFormModel getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(MetadataIntakeFormModel metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public PayloadIntakeFormModel getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(PayloadIntakeFormModel payload) {
        this.payload = payload;
    }

}
