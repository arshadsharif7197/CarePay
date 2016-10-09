
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Appointment implements Serializable {

    @SerializedName("metadata")
    @Expose
    private AppointmentsMetadataDto metadata;
    @SerializedName("payload")
    @Expose
    private AppointmentsPayloadModel payload;

    /**
     * 
     * @return
     *     The metadata
     */
    public AppointmentsMetadataDto getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(AppointmentsMetadataDto metadata) {
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
