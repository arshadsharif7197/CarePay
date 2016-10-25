package com.carecloud.carepaylibray.consentforms.models.labels;

/**
 * Created by Rahul on 10/21/16.
 */


import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormLabelsDTO {

    @SerializedName("Consent_for_medicare_title")
    @Expose
    private String consentForMedicareTitle;
    @SerializedName("sign_consent_for_medicare_title")
    @Expose
    private String signConsentForMedicareTitle;
    @SerializedName("consent_main_title")
    @Expose
    private String consentMainTitle;
    @SerializedName("consent_read_carefully_warning")
    @Expose
    private String consentReadCarefullyWarning;
    @SerializedName("Authorization_form_title")
    @Expose
    private String authorizationFormTitle;
    @SerializedName("sign_Authorization_form_title")
    @Expose
    private String signAuthorizationFormTitle;
    @SerializedName("hipaa_agreement_title")
    @Expose
    private String hipaaAgreementTitle;
    @SerializedName("sign_hipaa_agreement_title")
    @Expose
    private String signHipaaAgreementTitle;
    @SerializedName("confirm_signature_button")
    @Expose
    private String confirmSignatureButton;
    @SerializedName("read_again_button")
    @Expose
    private String readAgainButton;
    @SerializedName("sign_form_button")
    @Expose
    private String signFormButton;
    @SerializedName("minors_information")
    @Expose
    private String minorsInformation;
    @SerializedName("next_step_button")
    @Expose
    private String nextStepButton;
    @SerializedName("legal_first_name_label")
    @Expose
    private String legalFirstNameLabel;
    @SerializedName("legal_last_name_label")
    @Expose
    private String legalLastNameLabel;
    @SerializedName("legal_signature_label")
    @Expose
    private String legalSignatureLabel;
    @SerializedName("sign_clear_button")
    @Expose
    private String signClearButton;
    @SerializedName("before_signature__warning_text")
    @Expose
    private String beforeSignatureWarningText;
    @SerializedName("unable_to_sign_text")
    @Expose
    private String unableToSignText;
    @SerializedName("patient_signature_heading")
    @Expose
    private String patientSignatureHeading;
    @SerializedName("select_gender_label")
    @Expose
    private String selectGenderLabel;
    @SerializedName("select_date_label")
    @Expose
    private String selectDateLabel;
    @SerializedName("hipaa_confidentiality_agreement_text")
    @Expose
    private String hipaaConfidentialityAgreementText;
    @SerializedName("authorization_grant_text")
    @Expose
    private String authorizationGrantText;
    @SerializedName("authorization_legal_text")
    @Expose
    private String authorizationLegalText;
    @SerializedName("read_confirm_text")
    @Expose
    private String readConfirmText;
    @SerializedName("Consent_for_medicare_text")
    @Expose
    private String consentForMedicareText;

    /**
     * @return The consentForMedicareTitle
     */
    public String getConsentForMedicareTitle() {
        return StringUtil.isNullOrEmpty(consentForMedicareTitle) ? "undefined label" : consentForMedicareTitle;
    }

    /**
     * @param consentForMedicareTitle The Consent_for_medicare_title
     */
    public void setConsentForMedicareTitle(String consentForMedicareTitle) {
        this.consentForMedicareTitle = consentForMedicareTitle;
    }

    /**
     * @return The signConsentForMedicareTitle
     */
    public String getSignConsentForMedicareTitle() {
        return StringUtil.isNullOrEmpty(signConsentForMedicareTitle) ? "undefined label" : signConsentForMedicareTitle;
    }

    /**
     * @param signConsentForMedicareTitle The sign_consent_for_medicare_title
     */
    public void setSignConsentForMedicareTitle(String signConsentForMedicareTitle) {
        this.signConsentForMedicareTitle = signConsentForMedicareTitle;
    }

    /**
     * @return The consentMainTitle
     */
    public String getConsentMainTitle() {
        return StringUtil.isNullOrEmpty(consentMainTitle) ? "undefined label" : consentMainTitle;
    }

    /**
     * @param consentMainTitle The consent_main_title
     */
    public void setConsentMainTitle(String consentMainTitle) {
        this.consentMainTitle = consentMainTitle;
    }

    /**
     * @return The consentReadCarefullyWarning
     */
    public String getConsentReadCarefullyWarning() {
        return StringUtil.isNullOrEmpty(consentReadCarefullyWarning) ? "undefined label" : consentReadCarefullyWarning;
    }

    /**
     * @param consentReadCarefullyWarning The consent_read_carefully_warning
     */
    public void setConsentReadCarefullyWarning(String consentReadCarefullyWarning) {
        this.consentReadCarefullyWarning = consentReadCarefullyWarning;
    }

    /**
     * @return The authorizationFormTitle
     */
    public String getAuthorizationFormTitle() {
        return StringUtil.isNullOrEmpty(authorizationFormTitle) ? "undefined label" : authorizationFormTitle;
    }

    /**
     * @param authorizationFormTitle The Authorization_form_title
     */
    public void setAuthorizationFormTitle(String authorizationFormTitle) {
        this.authorizationFormTitle = authorizationFormTitle;
    }

    /**
     * @return The signAuthorizationFormTitle
     */
    public String getSignAuthorizationFormTitle() {
        return StringUtil.isNullOrEmpty(signAuthorizationFormTitle) ? "undefined label" : signAuthorizationFormTitle;
    }

    /**
     * @param signAuthorizationFormTitle The sign_Authorization_form_title
     */
    public void setSignAuthorizationFormTitle(String signAuthorizationFormTitle) {
        this.signAuthorizationFormTitle = signAuthorizationFormTitle;
    }

    /**
     * @return The hipaaAgreementTitle
     */
    public String getHipaaAgreementTitle() {

        return StringUtil.isNullOrEmpty(hipaaAgreementTitle) ? "undefined label" : hipaaAgreementTitle;
    }

    /**
     * @param hipaaAgreementTitle The hipaa_agreement_title
     */
    public void setHipaaAgreementTitle(String hipaaAgreementTitle) {
        this.hipaaAgreementTitle = hipaaAgreementTitle;
    }

    /**
     * @return The signHipaaAgreementTitle
     */
    public String getSignHipaaAgreementTitle() {

        return StringUtil.isNullOrEmpty(signHipaaAgreementTitle) ? "undefined label" : signHipaaAgreementTitle;
    }

    /**
     * @param signHipaaAgreementTitle The sign_hipaa_agreement_title
     */
    public void setSignHipaaAgreementTitle(String signHipaaAgreementTitle) {
        this.signHipaaAgreementTitle = signHipaaAgreementTitle;
    }

    /**
     * @return The confirmSignatureButton
     */
    public String getConfirmSignatureButton() {
        return StringUtil.isNullOrEmpty(confirmSignatureButton) ? "undefined label" : confirmSignatureButton;
    }

    /**
     * @param confirmSignatureButton The confirm_signature_button
     */
    public void setConfirmSignatureButton(String confirmSignatureButton) {
        this.confirmSignatureButton = confirmSignatureButton;
    }

    /**
     * @return The readAgainButton
     */
    public String getReadAgainButton() {

        return StringUtil.isNullOrEmpty(readAgainButton) ? "undefined label" : readAgainButton;
    }

    /**
     * @param readAgainButton The read_again_button
     */
    public void setReadAgainButton(String readAgainButton) {
        this.readAgainButton = readAgainButton;
    }

    /**
     * @return The signFormButton
     */
    public String getSignFormButton() {
        return StringUtil.isNullOrEmpty(signFormButton) ? "undefined label" : signFormButton;
    }

    /**
     * @param signFormButton The sign_form_button
     */
    public void setSignFormButton(String signFormButton) {
        this.signFormButton = signFormButton;
    }

    /**
     * @return The minorsInformation
     */
    public String getMinorsInformation() {
        return StringUtil.isNullOrEmpty(minorsInformation) ? "undefined label" : minorsInformation;
    }

    /**
     * @param minorsInformation The minors_information
     */
    public void setMinorsInformation(String minorsInformation) {
        this.minorsInformation = minorsInformation;
    }

    /**
     * @return The nextStepButton
     */
    public String getNextStepButton() {
        return StringUtil.isNullOrEmpty(nextStepButton) ? "undefined label" : nextStepButton;
    }

    /**
     * @param nextStepButton The next_step_button
     */
    public void setNextStepButton(String nextStepButton) {
        this.nextStepButton = nextStepButton;
    }

    /**
     * @return The legalFirstNameLabel
     */
    public String getLegalFirstNameLabel() {
        return StringUtil.isNullOrEmpty(legalFirstNameLabel) ? "undefined label" : legalFirstNameLabel;
    }

    /**
     * @param legalFirstNameLabel The legal_first_name_label
     */
    public void setLegalFirstNameLabel(String legalFirstNameLabel) {
        this.legalFirstNameLabel = legalFirstNameLabel;
    }

    /**
     * @return The legalLastNameLabel
     */
    public String getLegalLastNameLabel() {
        return StringUtil.isNullOrEmpty(legalLastNameLabel) ? "undefined label" : legalLastNameLabel;
    }

    /**
     * @param legalLastNameLabel The legal_last_name_label
     */
    public void setLegalLastNameLabel(String legalLastNameLabel) {
        this.legalLastNameLabel = legalLastNameLabel;
    }

    /**
     * @return The legalSignatureLabel
     */
    public String getLegalSignatureLabel() {
        return StringUtil.isNullOrEmpty(legalSignatureLabel) ? "undefined label" : legalSignatureLabel;
    }

    /**
     * @param legalSignatureLabel The legal_signature_label
     */
    public void setLegalSignatureLabel(String legalSignatureLabel) {
        this.legalSignatureLabel = legalSignatureLabel;
    }

    /**
     * @return The signClearButton
     */
    public String getSignClearButton() {
        return StringUtil.isNullOrEmpty(signClearButton) ? "undefined label" : signClearButton;
    }

    /**
     * @param signClearButton The sign_clear_button
     */
    public void setSignClearButton(String signClearButton) {
        this.signClearButton = signClearButton;
    }

    /**
     * @return The beforeSignatureWarningText
     */
    public String getBeforeSignatureWarningText() {
        return StringUtil.isNullOrEmpty(beforeSignatureWarningText) ? "undefined label" : beforeSignatureWarningText;
    }

    /**
     * @param beforeSignatureWarningText The before_signature__warning_text
     */
    public void setBeforeSignatureWarningText(String beforeSignatureWarningText) {
        this.beforeSignatureWarningText = beforeSignatureWarningText;
    }

    /**
     * @return The unableToSignText
     */
    public String getUnableToSignText() {
        return StringUtil.isNullOrEmpty(unableToSignText) ? "undefined label" : unableToSignText;
    }

    /**
     * @param unableToSignText The unable_to_sign_text
     */
    public void setUnableToSignText(String unableToSignText) {
        this.unableToSignText = unableToSignText;
    }

    /**
     * @return The patientSignatureHeading
     */
    public String getPatientSignatureHeading() {
        return StringUtil.isNullOrEmpty(patientSignatureHeading) ? "undefined label" : patientSignatureHeading;
    }

    /**
     * @param patientSignatureHeading The patient_signature_heading
     */
    public void setPatientSignatureHeading(String patientSignatureHeading) {
        this.patientSignatureHeading = patientSignatureHeading;
    }

    /**
     * @return The selectGenderLabel
     */
    public String getSelectGenderLabel() {
        return StringUtil.isNullOrEmpty(selectGenderLabel) ? "undefined label" : selectGenderLabel;
    }

    /**
     * @param selectGenderLabel The select_gender_label
     */
    public void setSelectGenderLabel(String selectGenderLabel) {
        this.selectGenderLabel = selectGenderLabel;
    }

    /**
     * @return The selectDateLabel
     */
    public String getSelectDateLabel() {
        return StringUtil.isNullOrEmpty(selectDateLabel) ? "undefined label" : selectDateLabel;
    }

    /**
     * @param selectDateLabel The select_date_label
     */
    public void setSelectDateLabel(String selectDateLabel) {
        this.selectDateLabel = selectDateLabel;
    }

    /**
     * @return The hipaaConfidentialityAgreementText
     */
    public String getHipaaConfidentialityAgreementText() {
        return StringUtil.isNullOrEmpty(hipaaConfidentialityAgreementText) ? "undefined label" : hipaaConfidentialityAgreementText;
    }

    /**
     * @param hipaaConfidentialityAgreementText The hipaa_confidentiality_agreement_text
     */
    public void setHipaaConfidentialityAgreementText(String hipaaConfidentialityAgreementText) {
        this.hipaaConfidentialityAgreementText = hipaaConfidentialityAgreementText;
    }

    /**
     * @return The authorizationGrantText
     */
    public String getAuthorizationGrantText() {
        return StringUtil.isNullOrEmpty(authorizationGrantText) ? "undefined label" : authorizationGrantText;
    }

    /**
     * @param authorizationGrantText The authorization_grant_text
     */
    public void setAuthorizationGrantText(String authorizationGrantText) {
        this.authorizationGrantText = authorizationGrantText;
    }

    /**
     * @return The authorizationLegalText
     */
    public String getAuthorizationLegalText() {
        return StringUtil.isNullOrEmpty(authorizationLegalText) ? "undefined label" : authorizationLegalText;
    }

    /**
     * @param authorizationLegalText The authorization_legal_text
     */
    public void setAuthorizationLegalText(String authorizationLegalText) {
        this.authorizationLegalText = authorizationLegalText;
    }

    /**
     * @return The readConfirmText
     */
    public String getReadConfirmText() {
        return StringUtil.isNullOrEmpty(readConfirmText) ? "undefined label" : readConfirmText;
    }

    /**
     * @param readConfirmText The read_confirm_text
     */
    public void setReadConfirmText(String readConfirmText) {
        this.readConfirmText = readConfirmText;
    }

    /**
     * @return The consentForMedicareText
     */
    public String getConsentForMedicareText() {
        return StringUtil.isNullOrEmpty(consentForMedicareText) ? "undefined label" : consentForMedicareText;
    }

    /**
     * @param consentForMedicareText The Consent_for_medicare_text
     */
    public void setConsentForMedicareText(String consentForMedicareText) {
        this.consentForMedicareText = consentForMedicareText;
    }

}
