package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenDTO {
    @SerializedName("metadata")
    @Expose
    private HomeScreenMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private HomeScreenPayloadDTO payload;
    @SerializedName("state")
    @Expose
    private String state;

    /**
     *
     * @return
     * The metadata
     */
    public HomeScreenMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(HomeScreenMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public HomeScreenPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(HomeScreenPayloadDTO payload) {
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
