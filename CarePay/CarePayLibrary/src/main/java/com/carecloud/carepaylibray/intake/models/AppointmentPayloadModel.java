
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AppointmentPayloadModel {

    @SerializedName("metadata")
    @Expose
    private AppointmentMetadataModel metadata = new AppointmentMetadataModel();
    @SerializedName("payload")
    @Expose
    private PayloadAppointmentModel payload = new PayloadAppointmentModel();

    /**
     * 
     * @return
     *     The metadata
     */
    public AppointmentMetadataModel getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(AppointmentMetadataModel metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public PayloadAppointmentModel getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(PayloadAppointmentModel payload) {
        this.payload = payload;
    }

}
