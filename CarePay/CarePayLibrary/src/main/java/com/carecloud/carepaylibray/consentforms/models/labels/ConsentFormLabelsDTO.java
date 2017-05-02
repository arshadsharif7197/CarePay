package com.carecloud.carepaylibray.consentforms.models.labels;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ConsentFormLabelsDTO implements Serializable {

    @SerializedName("sign_consent_for_medicare_title")
    @Expose
    private String signConsentForMedicareTitle;
    @SerializedName("confirm_signature_button")
    @Expose
    private String confirmSignatureButton;
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
    @SerializedName("signature_activity_title")
    @Expose
    private String signatureActivityTitle;
    @SerializedName("sign_consent_close_label")
    @Expose
    private String signConsentCloseLabel;

    /**
     * @return The signConsentForMedicareTitle
     */
    public String getSignConsentForMedicareTitle() {
        return StringUtil.isNullOrEmpty(signConsentForMedicareTitle) ? "undefined label" : signConsentForMedicareTitle;
    }

    /**
     * @return The confirmSignatureButton
     */
    public String getConfirmSignatureButton() {
        return StringUtil.isNullOrEmpty(confirmSignatureButton) ? "Not Defined" : confirmSignatureButton;
    }

    /**
     * @return The legalFirstNameLabel
     */
    public String getLegalFirstNameLabel() {
        return StringUtil.isNullOrEmpty(legalFirstNameLabel) ? "Not Defined"  : legalFirstNameLabel;
    }

    /**
     * @return The legalLastNameLabel
     */
    public String getLegalLastNameLabel() {
        return StringUtil.isNullOrEmpty(legalLastNameLabel) ? "Not Defined"  : legalLastNameLabel;
    }

    /**
     * @return The legalSignatureLabel
     */
    public String getLegalSignatureLabel() {
        return StringUtil.isNullOrEmpty(legalSignatureLabel) ? "Not Defined"  : legalSignatureLabel;
    }

    /**
     * @return The signClearButton
     */
    public String getSignClearButton() {
        return StringUtil.isNullOrEmpty(signClearButton) ? "Not Defined"  : signClearButton;
    }

    /**
     * @return The beforeSignatureWarningText
     */
    public String getBeforeSignatureWarningText() {
        return StringUtil.isNullOrEmpty(beforeSignatureWarningText) ? "Not Defined"  : beforeSignatureWarningText;
    }

    /**
     * @return The unableToSignText
     */
    public String getUnableToSignText() {
        return StringUtil.isNullOrEmpty(unableToSignText) ? "Not Defined" : unableToSignText;
    }

    /**
     * @return The patientSignatureHeading
     */
    public String getPatientSignatureHeading() {
        return StringUtil.isNullOrEmpty(patientSignatureHeading) ? "Not Defined"  : patientSignatureHeading;
    }

    /**
     * @return The signatureActivityTitle
     */
    public String getSignatureActivityTitleText() {
        return StringUtil.getLabelForView(signatureActivityTitle);
    }

    /**
     * @return The signConsentCloseLabel
     */
    public String getSignConsentCloseLabel() {
        return StringUtil.getLabelForView(signConsentCloseLabel);
    }
}
