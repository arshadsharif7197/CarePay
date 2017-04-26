package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */
@Deprecated
public class SignInTransitionsDTO {
    @SerializedName("authenticate")
    @Expose
    private TransitionDTO authenticate = new TransitionDTO();

    @SerializedName("sign_in")
    @Expose
    private TransitionDTO signIn = new TransitionDTO();

    @SerializedName("refresh")
    @Expose
    private TransitionDTO refresh = new TransitionDTO();

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

    public TransitionDTO getSignIn() {
        return signIn;
    }

    public void setSignIn(TransitionDTO signIn) {
        this.signIn = signIn;
    }

    public TransitionDTO getRefresh() {
        return refresh;
    }

    public void setRefresh(TransitionDTO refresh) {
        this.refresh = refresh;
    }
}
