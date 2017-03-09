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

    @SerializedName("sign_up")
    @Expose
    private TransitionDTO signUp = new TransitionDTO();

    @SerializedName("sign_in")
    @Expose
    private TransitionDTO signIn = new TransitionDTO();

    @SerializedName("refresh")
    @Expose
    private TransitionDTO refresh = new TransitionDTO();

    @SerializedName("sign_out")
    @Expose
    private TransitionDTO signOut = new TransitionDTO();

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

    public TransitionDTO getSignUp() {
        return signUp;
    }

    public void setSignUp(TransitionDTO signUp) {
        this.signUp = signUp;
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

    public TransitionDTO getSignOut() {
        return signOut;
    }

    public void setSignOut(TransitionDTO signOut) {
        this.signOut = signOut;
    }
}