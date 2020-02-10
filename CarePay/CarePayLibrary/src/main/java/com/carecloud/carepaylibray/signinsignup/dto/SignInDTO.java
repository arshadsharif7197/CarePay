package com.carecloud.carepaylibray.signinsignup.dto;

import com.carecloud.carepaylibray.interfaces.DTO;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */
public class SignInDTO implements DTO {

    @SerializedName("metadata")
    @Expose
    private SignInMetaDataDTO metadata = new SignInMetaDataDTO();
    @SerializedName("payload")
    @Expose
    private SignInPayloadDTO payload = new SignInPayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    public SignInMetaDataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(SignInMetaDataDTO metadata) {
        this.metadata = metadata;
    }

    public SignInPayloadDTO getPayload() {
        return payload;
    }

    public void setPayload(SignInPayloadDTO payload) {
        this.payload = payload;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
