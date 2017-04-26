package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Deprecated
public class SignInLablesDTO implements Serializable{

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

    @SerializedName("signup_sign_in_failed")
    @Expose
    private String signInFailed;

    @SerializedName("signup_sign_up_failed")
    @Expose
    private String signUpFailed;

    @SerializedName("signup_email")
    @Expose
    private String email;

    @SerializedName("signup_create_password")
    @Expose
    private String createPassword;

    @SerializedName("signup_repeat_password")
    @Expose
    private String repeatPassword;

    @SerializedName("signup_please_enter_email")
    @Expose
    private String pleaseEnterEmail;

    @SerializedName("signup_invalid_email")
    @Expose
    private String invalidEmail;

    @SerializedName("signup_please_enter_password")
    @Expose
    private String pleaseEnterPassword;

    @SerializedName("signup_invalid_password")
    @Expose
    private String invalidPassword;

    @SerializedName("signup_repeat_password_is_empty")
    @Expose
    private String repeatPasswordIsEmpty;

    @SerializedName("signup_passwords_do_not_match")
    @Expose
    private String passwordsDoNotMatch;

    @SerializedName("signup")
    @Expose
    private String signUp;

    @SerializedName("signup_confirm")
    @Expose
    private String confirm;

    @SerializedName("signup_confirm_confirmation_code_message_with_argument")
    @Expose
    private String confirmConfirmationCodeMessageWithArgument;

    @SerializedName("signup_confirm_confirmation_code_message")
    @Expose
    private String confirmConfirmationCodeMessage;

    @SerializedName("signup_confirm_request_for_confirmation")
    @Expose
    private String confirmRequestForConfirmation;

    @SerializedName("signup_confirm_cannot_empty_with_argument")
    @Expose
    private String confirmCannotEmptyWithArgument;

    @SerializedName("signup_confirm_confirmed_with_argument")
    @Expose
    private String confirmConfirmedWithArgument;

    @SerializedName("signup_confirm_success")
    @Expose
    private String confirmSuccess;

    @SerializedName("signup_confirm_confirmation_failed")
    @Expose
    private String confirmConfirmationFailed;

    @SerializedName("signup_confirm_confirm_your_account")
    @Expose
    private String confirmConfirmYourAccount;

    @SerializedName("signup_confirm_confirmation_code_sent")
    @Expose
    private String confirmConfirmationCodeSent;

    @SerializedName("signup_confirm_code_sent_to_with_argument")
    @Expose
    private String confirmCodeSentToWithArgument;

    @SerializedName("signup_confirm_confirmation_code_resend_failed")
    @Expose
    private String confirmConfirmationCodeResendFailed;

    @SerializedName("signup_confirm_confirmation_code_request_has_failed")
    @Expose
    private String confirmConfirmationCodeRequestHasFailed;

    @SerializedName("signup_confirm_ok")
    @Expose
    private String confirmOk;

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


    /**
     * Gets sign in failed.
     *
     * @return the sign in failed
     */
    public String getSignInFailed() {
        return StringUtil.getLabelForView(signInFailed);
    }

    /**
     * Sets sign in failed.
     *
     * @param signInFailed the sign in failed
     */
    public void setSignInFailed(String signInFailed) {
        this.signInFailed = signInFailed;
    }

    /**
     * Gets sign up failed.
     *
     * @return the sign up failed
     */
    public String getSignUpFailed() {
        return StringUtil.getLabelForView(signUpFailed);
    }

    /**
     * Sets sign up failed.
     *
     * @param signUpFailed the sign up failed
     */
    public void setSignUpFailed(String signUpFailed) {
        this.signUpFailed = signUpFailed;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return StringUtil.getLabelForView(email);
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets create password.
     *
     * @return the create password
     */
    public String getCreatePassword() {
        return StringUtil.getLabelForView(createPassword);
    }

    /**
     * Sets create password.
     *
     * @param createPassword the create password
     */
    public void setCreatePassword(String createPassword) {
        this.createPassword = createPassword;
    }

    /**
     * Gets repeat password.
     *
     * @return the repeat password
     */
    public String getRepeatPassword() {
        return StringUtil.getLabelForView(repeatPassword);
    }

    /**
     * Sets repeat password.
     *
     * @param repeatPassword the repeat password
     */
    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    /**
     * Gets please enter email.
     *
     * @return the please enter email
     */
    public String getPleaseEnterEmail() {
        return StringUtil.getLabelForView(pleaseEnterEmail);
    }

    /**
     * Sets please enter email.
     *
     * @param pleaseEnterEmail the please enter email
     */
    public void setPleaseEnterEmail(String pleaseEnterEmail) {
        this.pleaseEnterEmail = pleaseEnterEmail;
    }

    /**
     * Gets invalid email.
     *
     * @return the invalid email
     */
    public String getInvalidEmail() {
        return StringUtil.getLabelForView(invalidEmail);
    }

    /**
     * Sets invalid email.
     *
     * @param invalidEmail the invalid email
     */
    public void setInvalidEmail(String invalidEmail) {
        this.invalidEmail = invalidEmail;
    }

    /**
     * Gets please enter password.
     *
     * @return the please enter password
     */
    public String getPleaseEnterPassword() {
        return StringUtil.getLabelForView(pleaseEnterPassword);
    }

    /**
     * Sets please enter password.
     *
     * @param pleaseEnterPassword the please enter password
     */
    public void setPleaseEnterPassword(String pleaseEnterPassword) {
        this.pleaseEnterPassword = pleaseEnterPassword;
    }

    /**
     * Gets invalid password.
     *
     * @return the invalid password
     */
    public String getInvalidPassword() {
        return StringUtil.getLabelForView(invalidPassword);
    }

    /**
     * Sets invalid password.
     *
     * @param invalidPassword the invalid password
     */
    public void setInvalidPassword(String invalidPassword) {
        this.invalidPassword = invalidPassword;
    }

    /**
     * Gets repeat password is empty.
     *
     * @return the repeat password is empty
     */
    public String getRepeatPasswordIsEmpty() {
        return StringUtil.getLabelForView(repeatPasswordIsEmpty);
    }

    /**
     * Sets repeat password is empty.
     *
     * @param repeatPasswordIsEmpty the repeat password is empty
     */
    public void setRepeatPasswordIsEmpty(String repeatPasswordIsEmpty) {
        this.repeatPasswordIsEmpty = repeatPasswordIsEmpty;
    }

    /**
     * Gets passwords do not match.
     *
     * @return the passwords do not match
     */
    public String getPasswordsDoNotMatch() {
        return StringUtil.getLabelForView(passwordsDoNotMatch);
    }

    /**
     * Sets passwords do not match.
     *
     * @param passwordsDoNotMatch the passwords do not match
     */
    public void setPasswordsDoNotMatch(String passwordsDoNotMatch) {
        this.passwordsDoNotMatch = passwordsDoNotMatch;
    }

    /**
     * Gets sign up.
     *
     * @return the sign up
     */
    public String getSignUp() {
        return StringUtil.getLabelForView(signUp);
    }

    /**
     * Sets sign up.
     *
     * @param signUp the sign up
     */
    public void setSignUp(String signUp) {
        this.signUp = signUp;
    }

    /**
     * Gets confirm.
     *
     * @return the confirm
     */
    public String getConfirm() {
        return StringUtil.getLabelForView(confirm);
    }

    /**
     * Sets confirm.
     *
     * @param confirm the confirm
     */
    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    /**
     * Gets confirm confirmation code message with argument.
     *
     * @return the confirm confirmation code message with argument
     */
    public String getConfirmConfirmationCodeMessageWithArgument() {
        return StringUtil.getLabelForView(confirmConfirmationCodeMessageWithArgument);
    }

    /**
     * Sets confirm confirmation code message with argument.
     *
     * @param confirmConfirmationCodeMessageWithArgument the confirm confirmation code message with argument
     */
    public void setConfirmConfirmationCodeMessageWithArgument(String confirmConfirmationCodeMessageWithArgument) {
        this.confirmConfirmationCodeMessageWithArgument = confirmConfirmationCodeMessageWithArgument;
    }

    /**
     * Gets confirm confirmation code message.
     *
     * @return the confirm confirmation code message
     */
    public String getConfirmConfirmationCodeMessage() {
        return StringUtil.getLabelForView(confirmConfirmationCodeMessage);
    }

    /**
     * Sets confirm confirmation code message.
     *
     * @param confirmConfirmationCodeMessage the confirm confirmation code message
     */
    public void setConfirmConfirmationCodeMessage(String confirmConfirmationCodeMessage) {
        this.confirmConfirmationCodeMessage = confirmConfirmationCodeMessage;
    }

    /**
     * Gets confirm request for confirmation.
     *
     * @return the confirm request for confirmation
     */
    public String getConfirmRequestForConfirmation() {
        return StringUtil.getLabelForView(confirmRequestForConfirmation);
    }

    /**
     * Sets confirm request for confirmation.
     *
     * @param confirmRequestForConfirmation the confirm request for confirmation
     */
    public void setConfirmRequestForConfirmation(String confirmRequestForConfirmation) {
        this.confirmRequestForConfirmation = confirmRequestForConfirmation;
    }

    /**
     * Gets confirm cannot empty with argument.
     *
     * @return the confirm cannot empty with argument
     */
    public String getConfirmCannotEmptyWithArgument() {
        return StringUtil.getLabelForView(confirmCannotEmptyWithArgument);
    }

    /**
     * Sets confirm cannot empty with argument.
     *
     * @param confirmCannotEmptyWithArgument the confirm cannot empty with argument
     */
    public void setConfirmCannotEmptyWithArgument(String confirmCannotEmptyWithArgument) {
        this.confirmCannotEmptyWithArgument = confirmCannotEmptyWithArgument;
    }

    /**
     * Gets confirm confirmed with argument.
     *
     * @return the confirm confirmed with argument
     */
    public String getConfirmConfirmedWithArgument() {
        return StringUtil.getLabelForView(confirmConfirmedWithArgument);
    }

    /**
     * Sets confirm confirmed with argument.
     *
     * @param confirmConfirmedWithArgument the confirm confirmed with argument
     */
    public void setConfirmConfirmedWithArgument(String confirmConfirmedWithArgument) {
        this.confirmConfirmedWithArgument = confirmConfirmedWithArgument;
    }

    /**
     * Gets confirm success.
     *
     * @return the confirm success
     */
    public String getConfirmSuccess() {
        return StringUtil.getLabelForView(confirmSuccess);
    }

    /**
     * Sets confirm success.
     *
     * @param confirmSuccess the confirm success
     */
    public void setConfirmSuccess(String confirmSuccess) {
        this.confirmSuccess = confirmSuccess;
    }

    /**
     * Gets confirm confirmation failed.
     *
     * @return the confirm confirmation failed
     */
    public String getConfirmConfirmationFailed() {
        return StringUtil.getLabelForView(confirmConfirmationFailed);
    }

    /**
     * Sets confirm confirmation failed.
     *
     * @param confirmConfirmationFailed the confirm confirmation failed
     */
    public void setConfirmConfirmationFailed(String confirmConfirmationFailed) {
        this.confirmConfirmationFailed = confirmConfirmationFailed;
    }

    /**
     * Gets confirm confirm your account.
     *
     * @return the confirm confirm your account
     */
    public String getConfirmConfirmYourAccount() {
        return StringUtil.getLabelForView(confirmConfirmYourAccount);
    }

    /**
     * Sets confirm confirm your account.
     *
     * @param confirmConfirmYourAccount the confirm confirm your account
     */
    public void setConfirmConfirmYourAccount(String confirmConfirmYourAccount) {
        this.confirmConfirmYourAccount = confirmConfirmYourAccount;
    }

    /**
     * Gets confirm confirmation code sent.
     *
     * @return the confirm confirmation code sent
     */
    public String getConfirmConfirmationCodeSent() {
        return StringUtil.getLabelForView(confirmConfirmationCodeSent);
    }

    /**
     * Sets confirm confirmation code sent.
     *
     * @param confirmConfirmationCodeSent the confirm confirmation code sent
     */
    public void setConfirmConfirmationCodeSent(String confirmConfirmationCodeSent) {
        this.confirmConfirmationCodeSent = confirmConfirmationCodeSent;
    }

    /**
     * Gets confirm code sent to with argument.
     *
     * @return the confirm code sent to with argument
     */
    public String getConfirmCodeSentToWithArgument() {
        return StringUtil.getLabelForView(confirmCodeSentToWithArgument);
    }

    /**
     * Sets confirm code sent to with argument.
     *
     * @param confirmCodeSentToWithArgument the confirm code sent to with argument
     */
    public void setConfirmCodeSentToWithArgument(String confirmCodeSentToWithArgument) {
        this.confirmCodeSentToWithArgument = confirmCodeSentToWithArgument;
    }

    /**
     * Gets confirm confirmation code resend failed.
     *
     * @return the confirm confirmation code resend failed
     */
    public String getConfirmConfirmationCodeResendFailed() {
        return StringUtil.getLabelForView(confirmConfirmationCodeResendFailed);
    }

    /**
     * Sets confirm confirmation code resend failed.
     *
     * @param confirmConfirmationCodeResendFailed the confirm confirmation code resend failed
     */
    public void setConfirmConfirmationCodeResendFailed(String confirmConfirmationCodeResendFailed) {
        this.confirmConfirmationCodeResendFailed = confirmConfirmationCodeResendFailed;
    }

    /**
     * Gets confirm confirmation code request has failed.
     *
     * @return the confirm confirmation code request has failed
     */
    public String getConfirmConfirmationCodeRequestHasFailed() {
        return StringUtil.getLabelForView(confirmConfirmationCodeRequestHasFailed);
    }

    /**
     * Sets confirm confirmation code request has failed.
     *
     * @param confirmConfirmationCodeRequestHasFailed the confirm confirmation code request has failed
     */
    public void setConfirmConfirmationCodeRequestHasFailed(String confirmConfirmationCodeRequestHasFailed) {
        this.confirmConfirmationCodeRequestHasFailed = confirmConfirmationCodeRequestHasFailed;
    }

    /**
     * Gets confirm ok.
     *
     * @return the confirm ok
     */
    public String getConfirmOk() {
        return StringUtil.getLabelForView(confirmOk);
    }

    /**
     * Sets confirm ok.
     *
     * @param confirmOk the confirm ok
     */
    public void setConfirmOk(String confirmOk) {
        this.confirmOk = confirmOk;
    }
}