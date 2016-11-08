package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SignInSignUpTransitionsDTO {

    @SerializedName("authenticate")
    @Expose
    private AuthenticateDTO authenticate;

    /**
     *
     * @return
     * The authenticate
     */
    public AuthenticateDTO getAuthenticate() {
        return authenticate;
    }

    /**
     *
     * @param authenticate
     * The authenticate
     */
    public void setAuthenticate(AuthenticateDTO authenticate) {
        this.authenticate = authenticate;
    }

}