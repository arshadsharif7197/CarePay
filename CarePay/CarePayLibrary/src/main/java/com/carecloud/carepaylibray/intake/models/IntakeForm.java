
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class IntakeForm {

    @SerializedName("metadata")
    @Expose
    private MetadataIntakeFormModel metadata = new MetadataIntakeFormModel();
    @SerializedName("payload")
    @Expose
//    private PayloadIntakeFormModel payload = new PayloadIntakeFormModel();
    private JsonObject payload;

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

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

//    /**
//     *
//     * @return
//     *     The payload
//     */
//    public PayloadIntakeFormModel getPayload() {
//        return payload;
//    }
//
//    /**
//     *
//     * @param payload
//     *     The payload
//     */
//    public void setPayload(PayloadIntakeFormModel payload) {
//        this.payload = payload;
//    }

}
