
package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model for GET appointment result.
 */
public class AppointmentsResultModel implements Serializable, DTO {

    @SerializedName("metadata")
    @Expose
    private AppointmentMetadataModel metadata = new AppointmentMetadataModel();
    @SerializedName("payload")
    @Expose
    private AppointmentPayloadModel payload = new AppointmentPayloadModel();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * @return The metadata
     */
    public AppointmentMetadataModel getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(AppointmentMetadataModel metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public AppointmentPayloadModel getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(AppointmentPayloadModel payload) {
        this.payload = payload;
    }

    /**
     * @return The state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(String state) {
        this.state = state;
    }

}
