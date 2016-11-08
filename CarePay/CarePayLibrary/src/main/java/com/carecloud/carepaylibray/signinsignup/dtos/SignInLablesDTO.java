package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInLablesDTO {

    @SerializedName("signin_button")
    @Expose
    private String signinButton;
    @SerializedName("create_new_account_button")
    @Expose
    private String createNewAccountButton;
    @SerializedName("change_language_link")
    @Expose
    private String changeLanguageLink;
    @SerializedName("forgot_password_link")
    @Expose
    private String forgotPasswordLink;
    @SerializedName("signup_button")
    @Expose
    private String signupButton;
    @SerializedName("already_have_account_link")
    @Expose
    private String alreadyHaveAccountLink;
    @SerializedName("password_hint_text")
    @Expose
    private String passwordHintText;

    /**
     * @return The signinButton
     */
    public String getSigninButton() {
        return StringUtil.isNullOrEmpty(signinButton) ?
                CarePayConstants.NOT_DEFINED : signinButton;
    }

    /**
     * @param signinButton The signin_button
     */
    public void setSigninButton(String signinButton) {
        this.signinButton = signinButton;
    }

    /**
     * @return The createNewAccountButton
     */
    public String getCreateNewAccountButton() {
        return StringUtil.isNullOrEmpty(createNewAccountButton) ?
                CarePayConstants.NOT_DEFINED : createNewAccountButton;
    }

    /**
     * @param createNewAccountButton The create_new_account_button
     */
    public void setCreateNewAccountButton(String createNewAccountButton) {
        this.createNewAccountButton = createNewAccountButton;
    }

    /**
     * @return The changeLanguageLink
     */
    public String getChangeLanguageLink() {
        return StringUtil.isNullOrEmpty(changeLanguageLink) ?
                CarePayConstants.NOT_DEFINED : changeLanguageLink;
    }

    /**
     * @param changeLanguageLink The change_language_link
     */
    public void setChangeLanguageLink(String changeLanguageLink) {
        this.changeLanguageLink = changeLanguageLink;
    }

    /**
     * @return The forgotPasswordLink
     */
    public String getForgotPasswordLink() {
        return StringUtil.isNullOrEmpty(forgotPasswordLink) ?
                CarePayConstants.NOT_DEFINED : forgotPasswordLink;
    }

    /**
     * @param forgotPasswordLink The forgot_password_link
     */
    public void setForgotPasswordLink(String forgotPasswordLink) {
        this.forgotPasswordLink = forgotPasswordLink;
    }

    /**
     * @return The signupButton
     */
    public String getSignupButton() {
        return StringUtil.isNullOrEmpty(signupButton) ?
                CarePayConstants.NOT_DEFINED : signupButton;
    }

    /**
     * @param signupButton The signup_button
     */
    public void setSignupButton(String signupButton) {
        this.signupButton = signupButton;
    }

    /**
     * @return The alreadyHaveAccountLink
     */
    public String getAlreadyHaveAccountLink() {
        return StringUtil.isNullOrEmpty(alreadyHaveAccountLink) ?
                CarePayConstants.NOT_DEFINED : alreadyHaveAccountLink;
    }

    /**
     * @param alreadyHaveAccountLink The already_have_account_link
     */
    public void setAlreadyHaveAccountLink(String alreadyHaveAccountLink) {
        this.alreadyHaveAccountLink = alreadyHaveAccountLink;
    }

    /**
     * @return The passwordHintText
     */
    public String getPasswordHintText() {
        return StringUtil.isNullOrEmpty(passwordHintText) ?
                CarePayConstants.NOT_DEFINED : passwordHintText;
    }

    /**
     * @param passwordHintText The password_hint_text
     */
    public void setPasswordHintText(String passwordHintText) {
        this.passwordHintText = passwordHintText;
    }

}