package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class CheckInDTO  implements DTO{
    @SerializedName("metadata")
    @Expose
    private CheckInMetadataDTO metadata = new CheckInMetadataDTO();
    @SerializedName("payload")
    @Expose
    private CheckInPayloadDTO payload = new CheckInPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     *
     * @return
     * The metadata
     */
    public CheckInMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(CheckInMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public CheckInPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(CheckInPayloadDTO payload) {
        this.payload = payload;
    }

    /**
     *
     * @return
     * The state
     */
    public String getState() {
        return state;
    }

    /**
     *
     * @param state
     * The state
     */
    public void setState(String state) {
        this.state = state;
    }
}
