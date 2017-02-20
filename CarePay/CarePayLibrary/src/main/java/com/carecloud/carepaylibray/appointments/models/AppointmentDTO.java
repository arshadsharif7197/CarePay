
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model for appointment.
 */
public class  AppointmentDTO implements Serializable {

    @SerializedName("metadata")
    @Expose
    private AppointmentsMetadataDTO metadata = new AppointmentsMetadataDTO();
    @SerializedName("payload")
    @Expose
    private AppointmentsPayloadDTO payload = new AppointmentsPayloadDTO();

    /**
     * 
     * @return
     *     The metadata
     */
    public AppointmentsMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(AppointmentsMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public AppointmentsPayloadDTO getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(AppointmentsPayloadDTO payload) {
        this.payload = payload;
    }

}
