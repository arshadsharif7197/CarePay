package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class SignInTransitionsDTO {
    @SerializedName("authenticate")
    @Expose
    private SigninAuthenticateDTO authenticate;

    /**
     *
     * @return
     * The authenticate
     */
    public SigninAuthenticateDTO getAuthenticate() {
        return authenticate;
    }

    /**
     *
     * @param authenticate
     * The authenticate
     */
    public void setAuthenticate(SigninAuthenticateDTO authenticate) {
        this.authenticate = authenticate;
    }
}
