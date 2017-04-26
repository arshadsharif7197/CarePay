package com.carecloud.carepaylibray.signinsignup.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInLabelsDTO {

    @SerializedName("signin_button")
    @Expose
    private String signinButton;
    @SerializedName("signup_link")
    @Expose
    private String signupLink;
    @SerializedName("forgot_password")
    @Expose
    private String forgotPassword;
    @SerializedName("already_have_account_link")
    @Expose
    private String alreadyHaveAccountLink;
    @SerializedName("personal_info_personal_information")
    @Expose
    private String personalInfoPersonalInformation;
    @SerializedName("personal_info_identify_yourself")
    @Expose
    private String personalInfoIdentifyYourself;
    @SerializedName("personal_info_first_name")
    @Expose
    private String personalInfoFirstName;
    @SerializedName("personal_info_last_name")
    @Expose
    private String personalInfoLastName;
    @SerializedName("personal_info_phone_number")
    @Expose
    private String personalInfoPhoneNumber;
    @SerializedName("personal_info_date_of_birth")
    @Expose
    private String personalInfoDateOfBirth;
    @SerializedName("personal_info_select")
    @Expose
    private String personalInfoSelect;
    @SerializedName("personal_info_find_my_appointments")
    @Expose
    private String personalInfoFindMyAppointments;
    @SerializedName("signin_how_want_check_in")
    @Expose
    private String signinHowWantCheckIn;
    @SerializedName("signin_how_check_in_carepay_login")
    @Expose
    private String signinHowCheckInCarepayLogin;
    @SerializedName("sigin_how_check_in_scan_qr_code")
    @Expose
    private String siginHowCheckInScanQrCode;
    @SerializedName("sigin_how_check_in_manual_search")
    @Expose
    private String siginHowCheckInManualSearch;
    @SerializedName("sigin_how_check_in_create_carepay_account")
    @Expose
    private String siginHowCheckInCreateCarepayAccount;
    @SerializedName("go_back_label")
    @Expose
    private String siginHowCheckInGoBack;
    @SerializedName("invalid_qr_code_message")
    @Expose
    private String invalidQRCodeMessage;
    @SerializedName("loading_message")
    @Expose
    private String loadingMessage;
    @SerializedName("invalid_qr_code_title")
    @Expose
    private String invalidQRCodeTitle;
    @SerializedName("carepay_signin_title")
    @Expose
    private String carepaySigninTitle;
    //TODO: Resolve this
//    @SerializedName("sign_in_failed")
//    @Expose
//    private String signInFailed;
    @SerializedName("personal_info_incorrect_details")
    @Expose
    private String personalInfoIncorrectDetails;
    @SerializedName("choose_gender_label")
    @Expose
    private String chooseGenderLabel;
    @SerializedName("gender_label")
    @Expose
    private String genderLabel;
    @SerializedName("gender_cancel_label")
    @Expose
    private String genderCancelLabel;
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

    public String getSigninButton() {
        return signinButton;
    }

    public void setSigninButton(String signinButton) {
        this.signinButton = signinButton;
    }

    public String getSignupLink() {
        return signupLink;
    }

    public void setSignupLink(String signupLink) {
        this.signupLink = signupLink;
    }

    public String getForgotPassword() {
        return forgotPassword;
    }

    public void setForgotPassword(String forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    public String getAlreadyHaveAccountLink() {
        return alreadyHaveAccountLink;
    }

    public void setAlreadyHaveAccountLink(String alreadyHaveAccountLink) {
        this.alreadyHaveAccountLink = alreadyHaveAccountLink;
    }

    public String getPersonalInfoPersonalInformation() {
        return personalInfoPersonalInformation;
    }

    public void setPersonalInfoPersonalInformation(String personalInfoPersonalInformation) {
        this.personalInfoPersonalInformation = personalInfoPersonalInformation;
    }

    public String getPersonalInfoIdentifyYourself() {
        return personalInfoIdentifyYourself;
    }

    public void setPersonalInfoIdentifyYourself(String personalInfoIdentifyYourself) {
        this.personalInfoIdentifyYourself = personalInfoIdentifyYourself;
    }

    public String getPersonalInfoFirstName() {
        return personalInfoFirstName;
    }

    public void setPersonalInfoFirstName(String personalInfoFirstName) {
        this.personalInfoFirstName = personalInfoFirstName;
    }

    public String getPersonalInfoLastName() {
        return personalInfoLastName;
    }

    public void setPersonalInfoLastName(String personalInfoLastName) {
        this.personalInfoLastName = personalInfoLastName;
    }

    public String getPersonalInfoPhoneNumber() {
        return personalInfoPhoneNumber;
    }

    public void setPersonalInfoPhoneNumber(String personalInfoPhoneNumber) {
        this.personalInfoPhoneNumber = personalInfoPhoneNumber;
    }

    public String getPersonalInfoDateOfBirth() {
        return personalInfoDateOfBirth;
    }

    public void setPersonalInfoDateOfBirth(String personalInfoDateOfBirth) {
        this.personalInfoDateOfBirth = personalInfoDateOfBirth;
    }

    public String getPersonalInfoSelect() {
        return personalInfoSelect;
    }

    public void setPersonalInfoSelect(String personalInfoSelect) {
        this.personalInfoSelect = personalInfoSelect;
    }

    public String getPersonalInfoFindMyAppointments() {
        return personalInfoFindMyAppointments;
    }

    public void setPersonalInfoFindMyAppointments(String personalInfoFindMyAppointments) {
        this.personalInfoFindMyAppointments = personalInfoFindMyAppointments;
    }

    public String getSigninHowWantCheckIn() {
        return signinHowWantCheckIn;
    }

    public void setSigninHowWantCheckIn(String signinHowWantCheckIn) {
        this.signinHowWantCheckIn = signinHowWantCheckIn;
    }

    public String getSigninHowCheckInCarepayLogin() {
        return signinHowCheckInCarepayLogin;
    }

    public void setSigninHowCheckInCarepayLogin(String signinHowCheckInCarepayLogin) {
        this.signinHowCheckInCarepayLogin = signinHowCheckInCarepayLogin;
    }

    public String getSiginHowCheckInScanQrCode() {
        return siginHowCheckInScanQrCode;
    }

    public void setSiginHowCheckInScanQrCode(String siginHowCheckInScanQrCode) {
        this.siginHowCheckInScanQrCode = siginHowCheckInScanQrCode;
    }

    public String getSiginHowCheckInManualSearch() {
        return siginHowCheckInManualSearch;
    }

    public void setSiginHowCheckInManualSearch(String siginHowCheckInManualSearch) {
        this.siginHowCheckInManualSearch = siginHowCheckInManualSearch;
    }

    public String getSiginHowCheckInCreateCarepayAccount() {
        return siginHowCheckInCreateCarepayAccount;
    }

    public void setSiginHowCheckInCreateCarepayAccount(String siginHowCheckInCreateCarepayAccount) {
        this.siginHowCheckInCreateCarepayAccount = siginHowCheckInCreateCarepayAccount;
    }

    public String getSiginHowCheckInGoBack() {
        return siginHowCheckInGoBack;
    }

    public void setSiginHowCheckInGoBack(String siginHowCheckInGoBack) {
        this.siginHowCheckInGoBack = siginHowCheckInGoBack;
    }

    public String getInvalidQRCodeMessage() {
        return invalidQRCodeMessage;
    }

    public void setInvalidQRCodeMessage(String invalidQRCodeMessage) {
        this.invalidQRCodeMessage = invalidQRCodeMessage;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public String getInvalidQRCodeTitle() {
        return invalidQRCodeTitle;
    }

    public void setInvalidQRCodeTitle(String invalidQRCodeTitle) {
        this.invalidQRCodeTitle = invalidQRCodeTitle;
    }

    public String getCarepaySigninTitle() {
        return carepaySigninTitle;
    }

    public void setCarepaySigninTitle(String carepaySigninTitle) {
        this.carepaySigninTitle = carepaySigninTitle;
    }

    public String getPersonalInfoIncorrectDetails() {
        return personalInfoIncorrectDetails;
    }

    public void setPersonalInfoIncorrectDetails(String personalInfoIncorrectDetails) {
        this.personalInfoIncorrectDetails = personalInfoIncorrectDetails;
    }

    public String getChooseGenderLabel() {
        return chooseGenderLabel;
    }

    public void setChooseGenderLabel(String chooseGenderLabel) {
        this.chooseGenderLabel = chooseGenderLabel;
    }

    public String getGenderLabel() {
        return genderLabel;
    }

    public void setGenderLabel(String genderLabel) {
        this.genderLabel = genderLabel;
    }

    public String getGenderCancelLabel() {
        return genderCancelLabel;
    }

    public void setGenderCancelLabel(String genderCancelLabel) {
        this.genderCancelLabel = genderCancelLabel;
    }

    public String getCreateNewAccountButton() {
        return createNewAccountButton;
    }

    public void setCreateNewAccountButton(String createNewAccountButton) {
        this.createNewAccountButton = createNewAccountButton;
    }

    public String getChangeLanguageLink() {
        return changeLanguageLink;
    }

    public void setChangeLanguageLink(String changeLanguageLink) {
        this.changeLanguageLink = changeLanguageLink;
    }

    public String getForgotPasswordLink() {
        return forgotPasswordLink;
    }

    public void setForgotPasswordLink(String forgotPasswordLink) {
        this.forgotPasswordLink = forgotPasswordLink;
    }

    public String getSignupButton() {
        return signupButton;
    }

    public void setSignupButton(String signupButton) {
        this.signupButton = signupButton;
    }

    public String getPasswordHintText() {
        return passwordHintText;
    }

    public void setPasswordHintText(String passwordHintText) {
        this.passwordHintText = passwordHintText;
    }

    public String getSignInFailed() {
        return signInFailed;
    }

    public void setSignInFailed(String signInFailed) {
        this.signInFailed = signInFailed;
    }

    public String getSignUpFailed() {
        return signUpFailed;
    }

    public void setSignUpFailed(String signUpFailed) {
        this.signUpFailed = signUpFailed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatePassword() {
        return createPassword;
    }

    public void setCreatePassword(String createPassword) {
        this.createPassword = createPassword;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }

    public String getPleaseEnterEmail() {
        return pleaseEnterEmail;
    }

    public void setPleaseEnterEmail(String pleaseEnterEmail) {
        this.pleaseEnterEmail = pleaseEnterEmail;
    }

    public String getInvalidEmail() {
        return invalidEmail;
    }

    public void setInvalidEmail(String invalidEmail) {
        this.invalidEmail = invalidEmail;
    }

    public String getPleaseEnterPassword() {
        return pleaseEnterPassword;
    }

    public void setPleaseEnterPassword(String pleaseEnterPassword) {
        this.pleaseEnterPassword = pleaseEnterPassword;
    }

    public String getInvalidPassword() {
        return invalidPassword;
    }

    public void setInvalidPassword(String invalidPassword) {
        this.invalidPassword = invalidPassword;
    }

    public String getRepeatPasswordIsEmpty() {
        return repeatPasswordIsEmpty;
    }

    public void setRepeatPasswordIsEmpty(String repeatPasswordIsEmpty) {
        this.repeatPasswordIsEmpty = repeatPasswordIsEmpty;
    }

    public String getPasswordsDoNotMatch() {
        return passwordsDoNotMatch;
    }

    public void setPasswordsDoNotMatch(String passwordsDoNotMatch) {
        this.passwordsDoNotMatch = passwordsDoNotMatch;
    }

    public String getSignUp() {
        return signUp;
    }

    public void setSignUp(String signUp) {
        this.signUp = signUp;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }

    public String getConfirmConfirmationCodeMessageWithArgument() {
        return confirmConfirmationCodeMessageWithArgument;
    }

    public void setConfirmConfirmationCodeMessageWithArgument(String confirmConfirmationCodeMessageWithArgument) {
        this.confirmConfirmationCodeMessageWithArgument = confirmConfirmationCodeMessageWithArgument;
    }

    public String getConfirmConfirmationCodeMessage() {
        return confirmConfirmationCodeMessage;
    }

    public void setConfirmConfirmationCodeMessage(String confirmConfirmationCodeMessage) {
        this.confirmConfirmationCodeMessage = confirmConfirmationCodeMessage;
    }

    public String getConfirmRequestForConfirmation() {
        return confirmRequestForConfirmation;
    }

    public void setConfirmRequestForConfirmation(String confirmRequestForConfirmation) {
        this.confirmRequestForConfirmation = confirmRequestForConfirmation;
    }

    public String getConfirmCannotEmptyWithArgument() {
        return confirmCannotEmptyWithArgument;
    }

    public void setConfirmCannotEmptyWithArgument(String confirmCannotEmptyWithArgument) {
        this.confirmCannotEmptyWithArgument = confirmCannotEmptyWithArgument;
    }

    public String getConfirmConfirmedWithArgument() {
        return confirmConfirmedWithArgument;
    }

    public void setConfirmConfirmedWithArgument(String confirmConfirmedWithArgument) {
        this.confirmConfirmedWithArgument = confirmConfirmedWithArgument;
    }

    public String getConfirmSuccess() {
        return confirmSuccess;
    }

    public void setConfirmSuccess(String confirmSuccess) {
        this.confirmSuccess = confirmSuccess;
    }

    public String getConfirmConfirmationFailed() {
        return confirmConfirmationFailed;
    }

    public void setConfirmConfirmationFailed(String confirmConfirmationFailed) {
        this.confirmConfirmationFailed = confirmConfirmationFailed;
    }

    public String getConfirmConfirmYourAccount() {
        return confirmConfirmYourAccount;
    }

    public void setConfirmConfirmYourAccount(String confirmConfirmYourAccount) {
        this.confirmConfirmYourAccount = confirmConfirmYourAccount;
    }

    public String getConfirmConfirmationCodeSent() {
        return confirmConfirmationCodeSent;
    }

    public void setConfirmConfirmationCodeSent(String confirmConfirmationCodeSent) {
        this.confirmConfirmationCodeSent = confirmConfirmationCodeSent;
    }

    public String getConfirmCodeSentToWithArgument() {
        return confirmCodeSentToWithArgument;
    }

    public void setConfirmCodeSentToWithArgument(String confirmCodeSentToWithArgument) {
        this.confirmCodeSentToWithArgument = confirmCodeSentToWithArgument;
    }

    public String getConfirmConfirmationCodeResendFailed() {
        return confirmConfirmationCodeResendFailed;
    }

    public void setConfirmConfirmationCodeResendFailed(String confirmConfirmationCodeResendFailed) {
        this.confirmConfirmationCodeResendFailed = confirmConfirmationCodeResendFailed;
    }

    public String getConfirmConfirmationCodeRequestHasFailed() {
        return confirmConfirmationCodeRequestHasFailed;
    }

    public void setConfirmConfirmationCodeRequestHasFailed(String confirmConfirmationCodeRequestHasFailed) {
        this.confirmConfirmationCodeRequestHasFailed = confirmConfirmationCodeRequestHasFailed;
    }

    public String getConfirmOk() {
        return confirmOk;
    }

    public void setConfirmOk(String confirmOk) {
        this.confirmOk = confirmOk;
    }
}
