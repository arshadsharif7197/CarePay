
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Model for GET appointment availability data.
 */
public class AppointmentAvailabilityDataDTO implements Serializable {

    @SerializedName("metadata")
    @Expose
    private AppointmentAvailabilityMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private AppointmentAvailabilityPayloadDTO payload;

    /**
     * Gets metadata.
     *
     * @return the metadata
     */
    public AppointmentAvailabilityMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * Sets metadata.
     *
     * @param metadata the metadata
     */
    public void setMetadata(AppointmentAvailabilityMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * Gets payload.
     *
     * @return the payload
     */
    public AppointmentAvailabilityPayloadDTO getPayload() {
        return payload;
    }

    /**
     * Sets payload.
     *
     * @param payload the payload
     */
    public void setPayload(AppointmentAvailabilityPayloadDTO payload) {
        this.payload = payload;
    }
}
