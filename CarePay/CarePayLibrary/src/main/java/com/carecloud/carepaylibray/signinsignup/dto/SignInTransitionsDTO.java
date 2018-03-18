package com.carecloud.carepaylibray.signinsignup.dto;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInTransitionsDTO {

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

    @SerializedName("action")
    @Expose
    private TransitionDTO action = new TransitionDTO();

    @SerializedName("qrcode")
    @Expose
    private TransitionDTO qrcode = new TransitionDTO();

    @SerializedName("forgot_password")
    @Expose
    private TransitionDTO forgotPassword = new TransitionDTO();

    public TransitionDTO getAuthenticate() {
        return authenticate;
    }

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

    public TransitionDTO getAction() {
        return action;
    }

    public void setAction(TransitionDTO action) {
        this.action = action;
    }

    public TransitionDTO getQrcode() {
        return qrcode;
    }

    public void setQrcode(TransitionDTO qrcode) {
        this.qrcode = qrcode;
    }

    public TransitionDTO getForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(TransitionDTO forgotPassword) {
        this.forgotPassword = forgotPassword;
    }
}
