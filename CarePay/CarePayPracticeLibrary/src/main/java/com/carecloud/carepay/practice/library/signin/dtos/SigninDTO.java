package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */
@Deprecated
public class SigninDTO {
    @SerializedName("metadata")
    @Expose
    private SigninMetadataDTO metadata = new SigninMetadataDTO();
    @SerializedName("payload")
    @Expose
    private SigninPayloadDTO payload = new SigninPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     *
     * @return
     * The metadata
     */
    public SigninMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(SigninMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public SigninPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(SigninPayloadDTO payload) {
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
