package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class SignInSignUpTransitionsDTO {

    @SerializedName("authenticate")
    @Expose
    private TransitionDTO authenticate = new TransitionDTO();

    @SerializedName("language")
    @Expose
    private TransitionDTO language = new TransitionDTO();

    /**
     *
     * @return
     * The authenticate
     */
    public TransitionDTO getAuthenticate() {
        return authenticate;
    }

    /**
     *
     * @param authenticate
     * The authenticate
     */
    public void setAuthenticate(TransitionDTO authenticate) {
        this.authenticate = authenticate;
    }

    public TransitionDTO getLanguage() {
        return language;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }
}