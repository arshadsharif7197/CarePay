
package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
@Deprecated
public class SigninPatientModeLabelsDTO {

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
    @SerializedName("sign_in_failed")
    @Expose
    private String signInFailed;
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

    /**
     * 
     * @return
     *     The signinButton
     */
    public String getSigninButton() {
        return StringUtil.getLabelForView(signinButton);
    }

    /**
     * 
     * @param signinButton
     *     The signin_button
     */
    public void setSigninButton(String signinButton) {
        this.signinButton = signinButton;
    }

    /**
     * 
     * @return
     *     The signupLink
     */
    public String getSignupLink() {
        return StringUtil.getLabelForView(signupLink);
    }

    /**
     * 
     * @param signupLink
     *     The signup_link
     */
    public void setSignupLink(String signupLink) {
        this.signupLink = signupLink;
    }

    /**
     * 
     * @return
     *     The signupButton
     */
    public String getSignupButton() {
        return StringUtil.getLabelForView(signupButton);
    }

    /**
     * 
     * @param signupButton
     *     The signup_button
     */
    public void setSignupButton(String signupButton) {
        this.signupButton = signupButton;
    }

    /**
     * 
     * @return
     *     The forgotPassword
     */
    public String getForgotPassword() {
        return StringUtil.getLabelForView(forgotPassword) ;
    }

    /**
     * 
     * @param forgotPassword
     *     The forgot_password
     */
    public void setForgotPassword(String forgotPassword) {
        this.forgotPassword = forgotPassword;
    }

    /**
     * 
     * @return
     *     The alreadyHaveAccountLink
     */
    public String getAlreadyHaveAccountLink() {
        return StringUtil.getLabelForView(alreadyHaveAccountLink);
    }

    /**
     * 
     * @param alreadyHaveAccountLink
     *     The already_have_account_link
     */
    public void setAlreadyHaveAccountLink(String alreadyHaveAccountLink) {
        this.alreadyHaveAccountLink = alreadyHaveAccountLink;
    }

    /**
     * 
     * @return
     *     The personalInfoPersonalInformation
     */
    public String getPersonalInfoPersonalInformation() {
        return StringUtil.getLabelForView(personalInfoPersonalInformation);
    }

    /**
     * 
     * @param personalInfoPersonalInformation
     *     The personal_info_personal_information
     */
    public void setPersonalInfoPersonalInformation(String personalInfoPersonalInformation) {
        this.personalInfoPersonalInformation = personalInfoPersonalInformation;
    }

    /**
     * 
     * @return
     *     The personalInfoIdentifyYourself
     */
    public String getPersonalInfoIdentifyYourself() {
        return StringUtil.getLabelForView(personalInfoIdentifyYourself);
    }

    /**
     * 
     * @param personalInfoIdentifyYourself
     *     The personal_info_identify_yourself
     */
    public void setPersonalInfoIdentifyYourself(String personalInfoIdentifyYourself) {
        this.personalInfoIdentifyYourself = personalInfoIdentifyYourself;
    }

    /**
     * 
     * @return
     *     The personalInfoFirstName
     */
    public String getPersonalInfoFirstName() {
        return StringUtil.getLabelForView(personalInfoFirstName) ;
    }

    /**
     * 
     * @param personalInfoFirstName
     *     The personal_info_first_name
     */
    public void setPersonalInfoFirstName(String personalInfoFirstName) {
        this.personalInfoFirstName = personalInfoFirstName;
    }

    /**
     * 
     * @return
     *     The personalInfoLastName
     */
    public String getPersonalInfoLastName() {
        return StringUtil.getLabelForView(personalInfoLastName);
    }

    /**
     * 
     * @param personalInfoLastName
     *     The personal_info_last_name
     */
    public void setPersonalInfoLastName(String personalInfoLastName) {
        this.personalInfoLastName = personalInfoLastName;
    }

    /**
     * 
     * @return
     *     The personalInfoPhoneNumber
     */
    public String getPersonalInfoPhoneNumber() {
        return StringUtil.getLabelForView(personalInfoPhoneNumber);
    }

    /**
     * 
     * @param personalInfoPhoneNumber
     *     The personal_info_phone_number
     */
    public void setPersonalInfoPhoneNumber(String personalInfoPhoneNumber) {
        this.personalInfoPhoneNumber = personalInfoPhoneNumber;
    }

    /**
     * 
     * @return
     *     The personalInfoDateOfBirth
     */
    public String getPersonalInfoDateOfBirth() {
        return StringUtil.getLabelForView(personalInfoDateOfBirth) ;
    }

    /**
     * 
     * @param personalInfoDateOfBirth
     *     The personal_info_date_of_birth
     */
    public void setPersonalInfoDateOfBirth(String personalInfoDateOfBirth) {
        this.personalInfoDateOfBirth = personalInfoDateOfBirth;
    }

    /**
     * 
     * @return
     *     The personalInfoSelect
     */
    public String getPersonalInfoSelect() {
        return StringUtil.getLabelForView(personalInfoSelect);
    }

    /**
     * 
     * @param personalInfoSelect
     *     The personal_info_select
     */
    public void setPersonalInfoSelect(String personalInfoSelect) {
        this.personalInfoSelect = personalInfoSelect;
    }

    /**
     * 
     * @return
     *     The personalInfoFindMyAppointments
     */
    public String getPersonalInfoFindMyAppointments() {
        return StringUtil.getLabelForView(personalInfoFindMyAppointments);
    }

    /**
     * 
     * @param personalInfoFindMyAppointments
     *     The personal_info_find_my_appointments
     */
    public void setPersonalInfoFindMyAppointments(String personalInfoFindMyAppointments) {
        this.personalInfoFindMyAppointments = personalInfoFindMyAppointments;
    }

    /**
     * 
     * @return
     *     The signinHowWantCheckIn
     */
    public String getSigninHowWantCheckIn() {
        return StringUtil.getLabelForView(signinHowWantCheckIn);
    }

    /**
     * 
     * @param signinHowWantCheckIn
     *     The signin_how_want_check_in
     */
    public void setSigninHowWantCheckIn(String signinHowWantCheckIn) {
        this.signinHowWantCheckIn = signinHowWantCheckIn;
    }

    /**
     * 
     * @return
     *     The signinHowCheckInCarepayLogin
     */
    public String getSigninHowCheckInCarepayLogin() {
        return StringUtil.getLabelForView(signinHowCheckInCarepayLogin) ;
    }

    /**
     * 
     * @param signinHowCheckInCarepayLogin
     *     The signin_how_check_in_carepay_login
     */
    public void setSigninHowCheckInCarepayLogin(String signinHowCheckInCarepayLogin) {
        this.signinHowCheckInCarepayLogin = signinHowCheckInCarepayLogin;
    }

    /**
     * 
     * @return
     *     The siginHowCheckInScanQrCode
     */
    public String getSiginHowCheckInScanQrCode() {
        return StringUtil.getLabelForView(siginHowCheckInScanQrCode);
    }

    /**
     * 
     * @param siginHowCheckInScanQrCode
     *     The sigin_how_check_in_scan_qr_code
     */
    public void setSiginHowCheckInScanQrCode(String siginHowCheckInScanQrCode) {
        this.siginHowCheckInScanQrCode = siginHowCheckInScanQrCode;
    }

    /**
     * 
     * @return
     *     The siginHowCheckInManualSearch
     */
    public String getSiginHowCheckInManualSearch() {
        return StringUtil.getLabelForView(siginHowCheckInManualSearch);
    }

    /**
     * 
     * @param siginHowCheckInManualSearch
     *     The sigin_how_check_in_manual_search
     */
    public void setSiginHowCheckInManualSearch(String siginHowCheckInManualSearch) {
        this.siginHowCheckInManualSearch = siginHowCheckInManualSearch;
    }

    /**
     * 
     * @return
     *     The siginHowCheckInCreateCarepayAccount
     */
    public String getSiginHowCheckInCreateCarepayAccount() {
        return StringUtil.getLabelForView(siginHowCheckInCreateCarepayAccount) ;
    }

    /**
     * 
     * @param siginHowCheckInCreateCarepayAccount
     *     The sigin_how_check_in_create_carepay_account
     */
    public void setSiginHowCheckInCreateCarepayAccount(String siginHowCheckInCreateCarepayAccount) {
        this.siginHowCheckInCreateCarepayAccount = siginHowCheckInCreateCarepayAccount;
    }

    /**
     * 
     * @return
     *     The siginHowCheckInGoBack
     */
    public String getSiginHowCheckInGoBack() {
        return StringUtil.getLabelForView(siginHowCheckInGoBack);
    }

    /**
     * 
     * @param siginHowCheckInGoBack
     *     The sigin_how_check_in_go_back
     */
    public void setSiginHowCheckInGoBack(String siginHowCheckInGoBack) {
        this.siginHowCheckInGoBack = siginHowCheckInGoBack;
    }

    /**
     *
     * @return
     *     The carepaySigninTitle
     */
    public String getCarepaySigninTitle() {
        return StringUtil.getLabelForView(carepaySigninTitle);
    }

    /**
     *     The invalidQRCodeMessage
     */
    public String getInvalidQRCodeMessage() {
        return StringUtil.getLabelForView(invalidQRCodeMessage);
    }

    /**
     *
     * @param carepaySigninTitle
     *     The carepay_signin_title
     */
    public void setCarepaySigninTitle(String carepaySigninTitle) {
        this.carepaySigninTitle = carepaySigninTitle;
    }

    /**
     * @param invalidQRCodeMessage
     *     The invalidQRCodeMessage
     */
    public void setInvalidQRCodeMessage(String invalidQRCodeMessage) {
        this.invalidQRCodeMessage = invalidQRCodeMessage;
    }

    /**
     *
     * @return
     *     The loadingMessage
     */
    public String getLoadingMessage() {
        return StringUtil.getLabelForView(loadingMessage);
    }

    /**
     *
     * @param loadingMessage
     *     The loadingMessage
     */
    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    /**
     *
     * @return
     *     The invalidQRCodeTitle
     */
    public String getInvalidQRCodeTitle() {
        return StringUtil.getLabelForView(invalidQRCodeTitle);
    }

    /**
     *
     * @param invalidQRCodeTitle
     *     The invalidQRCodeTitle
     */
    public void setInvalidQRCodeTitle(String invalidQRCodeTitle) {
        this.invalidQRCodeTitle = invalidQRCodeTitle;
    }

    /**
     *
     * @return
     *     The signInFailed
     */
    public String getSignInFailed() {
        return StringUtil.getLabelForView(signInFailed);
    }

    /**
     *
     * @param signInFailed
     *     The signInFailed
     */
    public void setSignInFailed(String signInFailed) {
        this.signInFailed = signInFailed;
    }

    /**
     *
     * @return
     *     The personalInfoIncorrectDetails
     */
    public String getPersonalInfoIncorrectDetails() {
        return StringUtil.getLabelForView(personalInfoIncorrectDetails);
    }

    /**
     *
     * @param personalInfoIncorrectDetails
     *     The personalInfoIncorrectDetails
     */
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
}
