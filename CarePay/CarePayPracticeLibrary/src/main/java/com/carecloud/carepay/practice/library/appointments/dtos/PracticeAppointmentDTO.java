package com.carecloud.carepay.practice.library.appointments.dtos;

import com.carecloud.carepay.practice.library.checkin.dtos.CheckInMetadataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by cocampo on 2/28/17.
 */

public class PracticeAppointmentDTO {
    @SerializedName("metadata")
    @Expose
    private CheckInMetadataDTO metadata = new CheckInMetadataDTO();

    @SerializedName("payload")
    @Expose
    private PracticeAppointmentPayloadDTO payload = new PracticeAppointmentPayloadDTO();

    @SerializedName("state")
    @Expose
    private String state;

    /**
     * @return metadata
     */
    public CheckInMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * @return payload
     */
    public PracticeAppointmentPayloadDTO getPayload() {
        return payload;
    }

    /**
     * @return state
     */
    public String getState() {
        return state;
    }
}
