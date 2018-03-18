package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhir_pingale on 11/11/2016.
 */

public class CheckInStatusDataPayloadDTO {

    @SerializedName("metadata")
    @Expose
    private CheckInStatusDataPayloadMetadataDTO metadata = new CheckInStatusDataPayloadMetadataDTO();
    @SerializedName("payload")
    @Expose
    private CheckInStatusDataPayloadValueDTO payload = new CheckInStatusDataPayloadValueDTO();

    /**
     * @return The metadata
     */
    public CheckInStatusDataPayloadMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     * @param metadata The metadata
     */
    public void setMetadata(CheckInStatusDataPayloadMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * @return The payload
     */
    public CheckInStatusDataPayloadValueDTO getPayload() {
        return payload;
    }

    /**
     * @param payload The payload
     */
    public void setPayload(CheckInStatusDataPayloadValueDTO payload) {
        this.payload = payload;
    }
}
