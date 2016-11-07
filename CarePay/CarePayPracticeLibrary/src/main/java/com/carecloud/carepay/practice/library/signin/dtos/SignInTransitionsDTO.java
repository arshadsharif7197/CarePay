package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class SignInTransitionsDTO {
    @SerializedName("authenticate")
    @Expose
    private TransitionDTO authenticate;

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
}
