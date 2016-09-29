
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Appointment {

    @SerializedName("metadata")
    @Expose
    private AppointmentsMetadataModel metadata;
    @SerializedName("payload")
    @Expose
    private AppointmentsPayloadModel payload;

    /**
     * 
     * @return
     *     The metadata
     */
    public AppointmentsMetadataModel getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(AppointmentsMetadataModel metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public AppointmentsPayloadModel getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(AppointmentsPayloadModel payload) {
        this.payload = payload;
    }

}
