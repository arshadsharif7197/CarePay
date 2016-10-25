package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class SigninLabelsDTO {
    @SerializedName("signin_button")
    @Expose
    private String signinButton;
    @SerializedName("signup_link")
    @Expose
    private String signupLink;
    @SerializedName("signup_button")
    @Expose
    private String signupButton;
    @SerializedName("forgot_password")
    @Expose
    private String forgotPassword;
    @SerializedName("already_have_account_link")
    @Expose
    private String alreadyHaveAccountLink;

    /**
     *
     * @return
     * The signinButton
     */
    public String getSigninButton() {
        return signinButton;
    }

    /**
     *
     * @param signinButton
     * The signin_button
     */
    public void setSigninButton(String signinButton) {
        this.signinButton = signinButton;
    }

    /**
     *
     * @return
     * The signupLink
     */
    public String getSignupLink() {
        return signupLink;
    }

    /**
     *
     * @param signupLink
     * The signup_link
     */
    public void setSignupLink(String signupLink) {
        this.signupLink = signupLink;
    }

    /**
     *
     * @return
     * The signupButton
     */
    public String getSignupButton() {
        return signupButton;
    }

    /**
     *
     * @param signupButton
     * The signup_button
     */
    public void setSignupButton(String signupButton) {
        this.signupButton = signupButton;
    }

    /**
     *
     * @return
     * The forgotPassword
     */
    public String getForgotPassword() {
        return forgotPassword;
    }

    /**
     *
     * @param forgotPassword
     * The forgot_password
     */
    public void setForgotPassword(String forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    /**
     *
     * @return
     * The alreadyHaveAccountLink
     */
    public String getAlreadyHaveAccountLink() {
        return alreadyHaveAccountLink;
    }

    /**
     *
     * @param alreadyHaveAccountLink
     * The already_have_account_link
     */
    public void setAlreadyHaveAccountLink(String alreadyHaveAccountLink) {
        this.alreadyHaveAccountLink = alreadyHaveAccountLink;
    }
}
