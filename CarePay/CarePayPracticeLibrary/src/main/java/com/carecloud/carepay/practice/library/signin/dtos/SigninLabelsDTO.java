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
    @SerializedName("welcome_signin_text")
    @Expose
    private String welcomeSigninText;
    @SerializedName("signin_email_address")
    @Expose
    private String signinEmailAddress;
    @SerializedName("signin_password")
    @Expose
    private String signinPassword;
    @SerializedName("goback_button")
    @Expose
    private String gobackButton;
    @SerializedName("forgot_password")
    @Expose
    private String forgotPassword;

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
     * The welcomeSigninText
     */
    public String getWelcomeSigninText() {
        return welcomeSigninText;
    }

    /**
     *
     * @param welcomeSigninText
     * The welcome_signin_text
     */
    public void setWelcomeSigninText(String welcomeSigninText) {
        this.welcomeSigninText = welcomeSigninText;
    }

    /**
     *
     * @return
     * The signinEmailAddress
     */
    public String getSigninEmailAddress() {
        return signinEmailAddress;
    }

    /**
     *
     * @param signinEmailAddress
     * The signin_email_address
     */
    public void setSigninEmailAddress(String signinEmailAddress) {
        this.signinEmailAddress = signinEmailAddress;
    }

    /**
     *
     * @return
     * The signinPassword
     */
    public String getSigninPassword() {
        return signinPassword;
    }

    /**
     *
     * @param signinPassword
     * The signin_password
     */
    public void setSigninPassword(String signinPassword) {
        this.signinPassword = signinPassword;
    }

    /**
     *
     * @return
     * The gobackButton
     */
    public String getGobackButton() {
        return gobackButton;
    }

    /**
     *
     * @param gobackButton
     * The goback_button
     */
    public void setGobackButton(String gobackButton) {
        this.gobackButton = gobackButton;
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

}
