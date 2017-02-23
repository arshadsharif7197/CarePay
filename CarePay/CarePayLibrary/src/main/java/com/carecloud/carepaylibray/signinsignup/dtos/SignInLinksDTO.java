package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInLinksDTO {

    @SerializedName("self")
    @Expose
    private SignInSelfDTO self = new SignInSelfDTO();

    /**
     * @return The self
     */
    public SignInSelfDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(SignInSelfDTO self) {
        this.self = self;
    }

}