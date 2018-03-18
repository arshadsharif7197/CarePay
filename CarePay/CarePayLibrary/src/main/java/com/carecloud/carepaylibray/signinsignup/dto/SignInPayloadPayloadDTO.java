package com.carecloud.carepaylibray.signinsignup.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInPayloadPayloadDTO {

    @SerializedName("metadata")
    @Expose
    private SignInPayloadMetadata metadata = new SignInPayloadMetadata();
    @SerializedName("payload")
    @Expose
    private boolean payload;//ex isLoginSuccessful and isPersonalInfoCheckSuccessful

    public SignInPayloadMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SignInPayloadMetadata metadata) {
        this.metadata = metadata;
    }

    public boolean isPayload() {
        return payload;
    }

    public void setPayload(boolean payload) {
        this.payload = payload;
    }

    public boolean getPersonalInfoCheckSuccessful() {
        return payload;
    }

    public void setPersonalInfoCheckSuccessful(boolean payload) {
        this.payload = payload;
    }
}
