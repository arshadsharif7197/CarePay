package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;


public class SignInSignUpDTO {

    @SerializedName("metadata")
    @Expose
    private SignInMetaDataDTO metadata = new SignInMetaDataDTO();
    @SerializedName("payload")
    @Expose
    private SignInPayloadDTO payload = new SignInPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     *
     * @return
     * The metadata
     */
    public SignInMetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(SignInMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public SignInPayloadDTO getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(SignInPayloadDTO payload) {
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