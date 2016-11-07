package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class AppointmentDTO {

    @SerializedName("metadata")
    @Expose
    private AppointmentMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private AppointmentPayloadDTO payload;

    /**
     *
     * @return
     * The metadata
     */
    public AppointmentMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(AppointmentMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public AppointmentPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(AppointmentPayloadDTO payload) {
        this.payload = payload;
    }
}
