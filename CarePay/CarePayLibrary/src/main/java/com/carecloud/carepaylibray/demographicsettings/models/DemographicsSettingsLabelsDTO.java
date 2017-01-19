
package com.carecloud.carepaylibray.demographicsettings.models;


import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DemographicsSettingsLabelsDTO implements Serializable {
    @SerializedName("settings_heading")
    @Expose
    private String settingsHeading;
    @SerializedName("edit_button_label")
    @Expose
    private String editButtonLabel;
    @SerializedName("demographics_label")
    @Expose
    private String demographicsLabel;
    @SerializedName("documents_label")
    @Expose
    private String documentsLabel;
    @SerializedName("notifications_heading")
    @Expose
    private String notificationsHeading;
    @SerializedName("system_notifications_label")
    @Expose
    private String systemNotificationsHeading;
    @SerializedName("in_app_notifications_label")
    @Expose
    private String inAppNotificationsLabel;
    @SerializedName("email_label")
    @Expose
    private String emailLabel;
    @SerializedName("signout_button_label")
    @Expose
    private String signOutLabel;
    @SerializedName("credit_cards_label")
    @Expose
    private String creditCardsLabel;
    @SerializedName("profile_heading")
    @Expose
    private String profileHeadingLabel;
    @SerializedName("demographics_personal_info_label")
    @Expose
    private String demographics_personal_info_Label;
    @SerializedName("demographics_drivers_license_label")
    @Expose
    private String demographics_driver_license_Label;
    @SerializedName("demographics_take_pic_option")
    @Expose
    private String demographicsTakePhotoOption;
    @SerializedName("demographics_select_gallery_option")
    @Expose
    private String demographicsChooseFromLibraryOption;
    @SerializedName("demographics_select_capture_option_title")
    @Expose
    private String demographicsCaptureOptionsTitle;
    @SerializedName("demographics_cancel_label")
    @Expose
    private String demographicsCancelLabel;
    @SerializedName("demographics_address_label")
    @Expose
    private String demographicsAddressLabel;
    @SerializedName("demographics_demographics_label")
    @Expose
    private String demographicssectionLabel;
    @SerializedName("demographics_gender_label")
    @Expose
    private String demographicsGenderLabel;
    @SerializedName("demographics_race_label")
    @Expose
    private String demographicsRaceLabel;
    @SerializedName("demographics_ethnicity_label")
    @Expose
    private String demographicsEthnicityLabel;
    @SerializedName("demographics_preferred_language_label")
    @Expose
    private String demographicsLanguageLabel;
    @SerializedName("documents_document_type_label")
    @Expose
    private String documentsTypeLabel;
    @SerializedName("documents_scan_front_label")
    @Expose
    private String documentsScanFirstLabel;
    @SerializedName("documents_scan_back_label")
    @Expose
    private String documentsScanBackLabel;
    @SerializedName("documents_dl_number_label")
    @Expose
    private String documentsDlNumberLabel;
    @SerializedName("documents_dl_state_label")
    @Expose
    private String documentsDlStateLabel;
    @SerializedName("documents_health_insurance_label")
    @Expose
    private String documentsHealthInsuranceLabel;
    @SerializedName("documents_have_health_insurance_label")
    @Expose
    private String documentsHaveHealthInsuranceLabel;
    @SerializedName("demographics_add_another_insurance_link")
    @Expose
    private String documentsAddnotherInsuranceLabel;
    @SerializedName("demographics_golden_cross_Premium")
    @Expose
    private String documentsGoldenCrossLabel;
    @SerializedName("settings_change_password")
    @Expose
    private String settingschangePasswordLabel;
    @SerializedName("settings_messages")
    @Expose
    private String settingsMessagesLabel;
    @SerializedName("settings_current_password")
    @Expose
    private String settingsCurrentPasswordLabel;
    @SerializedName("settings_new_password")
    @Expose
    private String settingsNewPasswordLabel;
    @SerializedName("settings_repeat_new_password")
    @Expose
    private String settingRepeatNewPasswordLabel;
    @SerializedName("demographics_select_document")
    @Expose
    private String demographicsSelectDocumentLabel;
    @SerializedName("demographics_have_health_insurance")
    @Expose
    private String demographicsHaveInsurancesLabel;
    @SerializedName("demographics_have_multiple_insurances")
    @Expose
    private String demographicsMultipleInsurancesLabel;
    @SerializedName("demographics_license_label")
    @Expose
    private String demographicsLicenseLabel;
    @SerializedName("demographics_save_changes_label")
    @Expose
    private String demographicsSaveChangesLabel;
    @SerializedName("remove_link")
    @Expose
    private String demographicsRemoveLabel;
    @SerializedName("demographics_provider_label")
    @Expose
    private String demographicsProviderLabel;
    @SerializedName("demographics_plan_label")
    @Expose
    private String demographicsPlanLabel;
    @SerializedName("demographics_card_type_label")
    @Expose
    private String demographicsCardTypeLabel;
    @SerializedName("demographics_card_number_label")
    @Expose
    private String demographicsCardNumberLabel;
    @SerializedName("setting_change_name")
    @Expose
    private String demographicsChangeNameLabel;
    @SerializedName("setting_change_Email")
    @Expose
    private String demographicsChangeEmailLabel;

    /**
     *
     * @return systemNotificationsHeading
     */
    public String getSystemNotificationsHeading() {
        return StringUtil.getLabelForView(systemNotificationsHeading);
    }

    /**
     *
     * @param systemNotificationsHeading The systemNotificationsHeading
     */
    public void setSystemNotificationsHeading(String systemNotificationsHeading) {
        this.systemNotificationsHeading = systemNotificationsHeading;
    }

    /**
     *
     * @return settingsHeading
     */
    public String getSettingsHeading() {
        return StringUtil.getLabelForView(settingsHeading);
    }

    /**
     *
     * @param settingsHeading The settingsHeading
     */
    public void setSettingsHeading(String settingsHeading) {
        this.settingsHeading = settingsHeading;
    }

    /**
     *
     * @return editButtonLabel
     */
    public String getEditButtonLabel() {
        return StringUtil.getLabelForView(editButtonLabel);
    }

    /**
     *
     * @param editButtonLabel The editButtonLabel
     */
    public void setEditButtonLabel(String editButtonLabel) {
        this.editButtonLabel = editButtonLabel;
    }

    /**
     *
     * @return demographicsLabel
     */
    public String getDemographicsLabel() {
        return StringUtil.getLabelForView(demographicsLabel);
    }

    /**
     *
     * @param demographicsLabel The demographicsLabel
     */
    public void setDemographicsLabel(String demographicsLabel) {
        this.demographicsLabel = demographicsLabel;
    }

    /**
     *
     * @return documentsLabel
     */
    public String getDocumentsLabel() {
        return StringUtil.getLabelForView(documentsLabel);
    }

    /**
     *
     * @param documentsLabel The documentsLabel
     */
    public void setDocumentsLabel(String documentsLabel) {
        this.documentsLabel = documentsLabel;
    }

    /**
     *
     * @return notificationsHeading
     */
    public String getNotificationsHeading() {
        return StringUtil.getLabelForView(notificationsHeading);
    }

    /**
     *
     * @param notificationsHeading The notificationsHeading
     */
    public void setNotificationsHeading(String notificationsHeading) {
        this.notificationsHeading = notificationsHeading;
    }

    /**
     *
     * @return inAppNotificationsLabel
     */
    public String getInAppNotificationsLabel() {
        return StringUtil.getLabelForView(inAppNotificationsLabel);
    }

    /**
     *
     * @param inAppNotificationsLabel The inAppNotificationsLabel
     */
    public void setInAppNotificationsLabel(String inAppNotificationsLabel) {
        this.inAppNotificationsLabel = inAppNotificationsLabel;
    }

    /**
     *
     * @return emailLabel
     */
    public String getEmailLabel() {
        return StringUtil.getLabelForView(emailLabel);
    }

    /**
     *
     * @param emailLabel The emailLabel
     */
    public void setEmailLabel(String emailLabel) {
        this.emailLabel = emailLabel;
    }

    /**
     *
     * @return signOutLabel
     */
    public String getSignOutLabel() {
        return StringUtil.getLabelForView(signOutLabel);
    }

    /**
     *
     * @param signOutLabel The signOutLabel
     */
    public void setSignOutLabel(String signOutLabel) {
        this.signOutLabel = signOutLabel;
    }

    /**
     *
     * @return creditCardsLabel
     */
    public String getCreditCardsLabel() {
        return StringUtil.getLabelForView(creditCardsLabel);
    }

    /**
     *
     * @param creditCardsLabel The creditCardsLabel
     */
    public void setCreditCardsLabel(String creditCardsLabel) {
        this.creditCardsLabel = creditCardsLabel;
    }

    /**
     *
     * @return profileHeadingLabel
     */
    public String getProfileHeadingLabel() {
        return StringUtil.getLabelForView(profileHeadingLabel);
    }

    /**
     *
     * @param profileHeadingLabel The profileHeadingLabel
     */
    public void setProfileHeadingLabel(String profileHeadingLabel) {
        this.profileHeadingLabel = profileHeadingLabel;
    }

    /**
     *
     * @return demographics_personal_info_Label
     */
    public String getDemographics_personal_info_Label() {
        return StringUtil.getLabelForView(demographics_personal_info_Label);
    }

    /**
     *
     * @param demographics_personal_info_Label The demographics_personal_info_Label
     */
    public void setDemographics_personal_info_Label(String demographics_personal_info_Label) {
        this.demographics_personal_info_Label = demographics_personal_info_Label;
    }

    /**
     *
     * @return demographics_driver_license_Label
     */
    public String getDemographics_driver_license_Label() {
        return StringUtil.getLabelForView(demographics_driver_license_Label);
    }

    /**
     *
     * @param demographics_driver_license_Label The demographics_driver_license_Label
     */
    public void setDemographics_driver_license_Label(String demographics_driver_license_Label) {
        this.demographics_driver_license_Label = demographics_driver_license_Label;
    }

    /**
     *
     * @return demographicsCaptureOptionsTitle
     */
    public String getDemographicsCaptureOptionsTitle() {
        return StringUtil.getLabelForView(demographicsCaptureOptionsTitle);
    }

    /**
     *
     * @param demographicsCaptureOptionsTitle The demographicsCaptureOptionsTitle
     */
    public void setDemographicsCaptureOptionsTitle(String demographicsCaptureOptionsTitle) {
        this.demographicsCaptureOptionsTitle = demographicsCaptureOptionsTitle;
    }

    /**
     *
     * @return demographicsTakePhotoOption
     */
    public String getDemographicsTakePhotoOption() {
        return StringUtil.getLabelForView(demographicsTakePhotoOption);
    }

    /**
     *
     * @param demographicsTakePhotoOption The demographicsTakePhotoOption
     */
    public void setDemographicsTakePhotoOption(String demographicsTakePhotoOption) {
        this.demographicsTakePhotoOption = demographicsTakePhotoOption;
    }

    /**
     *
     * @return demographicsChooseFromLibraryOption
     */
    public String getDemographicsChooseFromLibraryOption() {
        return StringUtil.getLabelForView(demographicsChooseFromLibraryOption);
    }

    /**
     *
     * @param demographicsChooseFromLibraryOption The demographicsChooseFromLibraryOption
     */
    public void setDemographicsChooseFromLibraryOption(String demographicsChooseFromLibraryOption) {
        this.demographicsChooseFromLibraryOption = demographicsChooseFromLibraryOption;
    }

    /**
     *
     * @return demographicsCancelLabel
     */
    public String getDemographicsCancelLabel() {
        return StringUtil.getLabelForView(demographicsCancelLabel);

    }

    /**
     *
     * @param demographicsCancelLabel The demographicsCancelLabel
     */
    public void setDemographicsCancelLabel(String demographicsCancelLabel) {
        this.demographicsCancelLabel = demographicsCancelLabel;
    }

    /**
     *
     * @return demographicssectionLabel
     */
    public String getDemographicSctionLabel() {
        return StringUtil.getLabelForView(demographicssectionLabel);
    }

    /**
     *
     * @param demographicssectionLabel demographicssectionLabel
     */
    public void setDemographicssectionLabel(String demographicssectionLabel) {
        this.demographicssectionLabel = demographicssectionLabel;
    }

    /**
     *
     * @return demographicsAddressLabel
     */
    public String getDemographicsAddressLabel() {
        return StringUtil.getLabelForView(demographicsAddressLabel);
    }

    /**
     *
     * @param demographicsAddressLabel demographicsAddressLabel
     */
    public void setDemographicsAddressLabel(String demographicsAddressLabel) {
        this.demographicsAddressLabel = demographicsAddressLabel;
    }

    /**
     *
     * @return demographicsGenderLabel
     */
    public String getDemographicsGenderLabel() {
        return StringUtil.getLabelForView(demographicsGenderLabel);
    }

    /**
     *
     * @param demographicsGenderLabel demographicsGenderLabel
     */
    public void setDemographicsGenderLabel(String demographicsGenderLabel) {
        this.demographicsGenderLabel = demographicsGenderLabel;
    }

    /**
     *
     * @return demographicsRaceLabel
     */
    public String getDemographicsRaceLabel() {
        return StringUtil.getLabelForView(demographicsRaceLabel);
    }

    /**
     *
     * @param demographicsRaceLabel demographicsRaceLabel
     */
    public void setDemographicsRaceLabel(String demographicsRaceLabel) {
        this.demographicsRaceLabel = demographicsRaceLabel;
    }

    /**
     *
     * @return demographicsEthnicityLabel
     */
    public String getDemographicsEthnicityLabel() {
        return StringUtil.getLabelForView(demographicsEthnicityLabel);
    }

    /**
     *
     * @param demographicsEthnicityLabel demographicsEthnicityLabel
     */
    public void setDemographicsEthnicityLabel(String demographicsEthnicityLabel) {
        this.demographicsEthnicityLabel = demographicsEthnicityLabel;
    }

    /**
     *
     * @return demographicsLanguageLabel
     */
    public String getDemographicsLanguageLabel() {
        return StringUtil.getLabelForView(demographicsLanguageLabel);
    }

    /**
     *
     * @param demographicsLanguageLabel demographicsLanguageLabel
     */
    public void setDemographicsLanguageLabel(String demographicsLanguageLabel) {
        this.demographicsLanguageLabel = demographicsLanguageLabel;
    }

    /**
     *
     * @return documentsTypeLabel
     */
    public String getDocumentsTypeLabel() {
        return StringUtil.getLabelForView(documentsTypeLabel);
    }

    /**
     *
     * @param documentsTypeLabel The documentsTypeLabel
     */
    public void setDocumentsTypeLabel(String documentsTypeLabel) {
        this.documentsTypeLabel = documentsTypeLabel;
    }

    /**
     *
     * @return documentsScanFirstLabel
     */
    public String getDocumentsScanFirstLabel() {
        return StringUtil.getLabelForView(documentsScanFirstLabel);
    }

    /**
     *
     * @param documentsScanFirstLabel The documentsScanFirstLabel
     */
    public void setDocumentsScanFirstLabel(String documentsScanFirstLabel) {
        this.documentsScanFirstLabel = documentsScanFirstLabel;
    }

    /**
     *
     * @return documentsScanBackLabel
     */
    public String getDocumentsScanBackLabel() {
        return StringUtil.getLabelForView(documentsScanBackLabel);
    }

    /**
     *
     * @param documentsScanBackLabel The documentsScanBackLabel
     */
    public void setDocumentsScanBackLabel(String documentsScanBackLabel) {
        this.documentsScanBackLabel = documentsScanBackLabel;
    }

    /**
     *
     * @return documentsDlNumberLabel
     */
    public String getDocumentsDlNumberLabel() {
        return StringUtil.getLabelForView(documentsDlNumberLabel);
    }

    /**
     *
     * @param documentsDlNumberLabel The documentsDlNumberLabel
     */
    public void setDocumentsDlNumberLabel(String documentsDlNumberLabel) {
        this.documentsDlNumberLabel = documentsDlNumberLabel;
    }

    /**
     *
     * @return documentsDlStateLabel
     */
    public String getDocumentsDlStateLabel() {
        return StringUtil.getLabelForView(documentsDlStateLabel);
    }

    /**
     *
     * @param documentsDlStateLabel The documentsDlStateLabel
     */
    public void setDocumentsDlStateLabel(String documentsDlStateLabel) {
        this.documentsDlStateLabel = documentsDlStateLabel;
    }

    /**
     *
     * @return documentsHealthInsuranceLabel
     */
    public String getDocumentsHealthInsuranceLabel() {
        return StringUtil.getLabelForView(documentsHealthInsuranceLabel);
    }

    /**
     *
     * @param documentsHealthInsuranceLabel The documentsHealthInsuranceLabel
     */
    public void setDocumentsHealthInsuranceLabel(String documentsHealthInsuranceLabel) {
        this.documentsHealthInsuranceLabel = documentsHealthInsuranceLabel;
    }

    /**
     *
     * @return documentsHaveHealthInsuranceLabel
     */
    public String getDocumentsHaveHealthInsuranceLabel() {
        return StringUtil.getLabelForView(documentsHaveHealthInsuranceLabel);
    }

    /**
     *
     * @param documentsHaveHealthInsuranceLabel The documentsHaveHealthInsuranceLabel
     */
    public void setDocumentsHaveHealthInsuranceLabel(String documentsHaveHealthInsuranceLabel) {
        this.documentsHaveHealthInsuranceLabel = documentsHaveHealthInsuranceLabel;
    }

    /**
     *
     * @return documentsAddnotherInsuranceLabel
     */
    public String getDocumentsAddnotherInsuranceLabel() {
        return StringUtil.getLabelForView(documentsAddnotherInsuranceLabel);
    }

    /**
     *
     * @param documentsAddnotherInsuranceLabel The documentsAddnotherInsuranceLabel
     */
    public void setDocumentsAddnotherInsuranceLabel(String documentsAddnotherInsuranceLabel) {
        this.documentsAddnotherInsuranceLabel = documentsAddnotherInsuranceLabel;
    }

    /**
     *
     * @return documentsGoldenCrossLabel
     */
    public String getDocumentsGoldenCrossLabel() {
        return StringUtil.getLabelForView(documentsGoldenCrossLabel);
    }

    /**
     *
     * @param documentsGoldenCrossLabel The documentsGoldenCrossLabel
     */
    public void setDocumentsGoldenCrossLabel(String documentsGoldenCrossLabel) {
        this.documentsGoldenCrossLabel = documentsGoldenCrossLabel;
    }

    /**
     *
     * @return settingschangePasswordLabel
     */
    public String getSettingschangePasswordLabel() {
        return StringUtil.getLabelForView(settingschangePasswordLabel);
    }

    /**
     *
     * @param settingschangePasswordLabel The settingschangePasswordLabel
     */
    public void setSettingschangePasswordLabel(String settingschangePasswordLabel) {
        this.settingschangePasswordLabel = settingschangePasswordLabel;
    }

    /**
     *
     * @return settingsMessagesLabel
     */
    public String getSettingsMessagesLabel() {
        return StringUtil.getLabelForView(settingsMessagesLabel);
    }

    /**
     *
     * @param settingsMessagesLabel The settingsMessagesLabel
     */
    public void setSettingsMessagesLabel(String settingsMessagesLabel) {
        this.settingsMessagesLabel = settingsMessagesLabel;
    }

    /**
     *
     * @return settingsCurrentPasswordLabel
     */
    public String getSettingsCurrentPasswordLabel() {
        return StringUtil.getLabelForView(settingsCurrentPasswordLabel);
    }

    /**
     *
     * @param settingsCurrentPasswordLabel The settingsCurrentPasswordLabel
     */
    public void setSettingsCurrentPasswordLabel(String settingsCurrentPasswordLabel) {
        this.settingsCurrentPasswordLabel = settingsCurrentPasswordLabel;
    }

    /**
     *
     * @return settingsNewPasswordLabel
     */
    public String getSettingsNewPasswordLabel() {
        return StringUtil.getLabelForView(settingsNewPasswordLabel);
    }

    /**
     *
     * @param settingsNewPasswordLabel The settingsNewPasswordLabel
     */
    public void setSettingsNewPasswordLabel(String settingsNewPasswordLabel) {
        this.settingsNewPasswordLabel = settingsNewPasswordLabel;
    }

    /**
     *
     * @return settingRepeatNewPasswordLabel
     */
    public String getSettingRepeatNewPasswordLabel() {
        return StringUtil.getLabelForView(settingRepeatNewPasswordLabel);
    }

    /**
     *
     * @param settingRepeatNewPasswordLabel The settingRepeatNewPasswordLabel
     */
    public void setSettingRepeatNewPasswordLabel(String settingRepeatNewPasswordLabel) {
        this.settingRepeatNewPasswordLabel = settingRepeatNewPasswordLabel;
    }

    /**
     *
     * @return demographicsSelectDocumentLabel
     */
    public String getDemographicsSelectDocumentLabel() {
        return StringUtil.getLabelForView(demographicsSelectDocumentLabel);

    }

    /**
     *
     * @param demographicsSelectDocumentLabel The demographicsSelectDocumentLabel
     */
    public void setDemographicsSelectDocumentLabel(String demographicsSelectDocumentLabel) {
        this.demographicsSelectDocumentLabel = demographicsSelectDocumentLabel;
    }

    /**
     *
     * @return demographicsHaveInsurancesLabel
     */
    public String getDemographicsHaveInsurancesLabel() {
        return StringUtil.getLabelForView(demographicsHaveInsurancesLabel);

    }

    /**
     *
     * @param demographicsHaveInsurancesLabel The demographicsHaveInsurancesLabel
     */
    public void setDemographicsHaveInsurancesLabel(String demographicsHaveInsurancesLabel) {
        this.demographicsHaveInsurancesLabel = demographicsHaveInsurancesLabel;
    }

    /**
     *
     * @return demographicsMultipleInsurancesLabel
     */
    public String getDemographicsMultipleInsurancesLabel() {
        return StringUtil.getLabelForView(demographicsMultipleInsurancesLabel);
    }

    /**
     *
     * @param demographicsMultipleInsurancesLabel The demographicsMultipleInsurancesLabel
     */
    public void setDemographicsMultipleInsurancesLabel(String demographicsMultipleInsurancesLabel) {
        this.demographicsMultipleInsurancesLabel = demographicsMultipleInsurancesLabel;
    }

    /**
     *
     * @return demographicsLicenseLabel
     */
    public String getDemographicsLicenseLabel() {
        return StringUtil.getLabelForView(demographicsLicenseLabel);
    }

    /**
     *
     * @param demographicsLicenseLabel The demographicsLicenseLabel
     */
    public void setDemographicsLicenseLabel(String demographicsLicenseLabel) {
        this.demographicsLicenseLabel = demographicsLicenseLabel;
    }

    /**
     *
     * @return demographicsSaveChangesLabel
     */
    public String getDemographicsSaveChangesLabel() {
        return StringUtil.getLabelForView(demographicsSaveChangesLabel);

    }

    /**
     *
     * @param demographicsSaveChangesLabel demographicsSaveChangesLabel
     */
    public void setDemographicsSaveChangesLabel(String demographicsSaveChangesLabel) {
        this.demographicsSaveChangesLabel = demographicsSaveChangesLabel;
    }

    /**
     *
     * @return demographicsRemoveLabel
     */
    public String getDemographicsRemoveLabel() {
        return StringUtil.getLabelForView(demographicsRemoveLabel);

    }

    /**
     *
     * @param demographicsRemoveLabel The demographicsRemoveLabel
     */
    public void setDemographicsRemoveLabel(String demographicsRemoveLabel) {
        this.demographicsRemoveLabel = demographicsRemoveLabel;
    }

    /**
     *
     * @return demographicsProviderLabel
     */
    public String getDemographicsProviderLabel() {
        return StringUtil.getLabelForView(demographicsProviderLabel);
    }

    /**
     *
     * @param demographicsProviderLabel The demographicsProviderLabel
     */
    public void setDemographicsProviderLabel(String demographicsProviderLabel) {
        this.demographicsProviderLabel = demographicsProviderLabel;
    }

    /**
     *
     * @return demographicsPlanLabel
     */
    public String getDemographicsPlanLabel() {
        return StringUtil.getLabelForView(demographicsPlanLabel);
    }

    /**
     *
     * @param demographicsPlanLabel The demographicsPlanLabel
     */
    public void setDemographicsPlanLabel(String demographicsPlanLabel) {
        this.demographicsPlanLabel = demographicsPlanLabel;
    }

    /**
     *
     * @return demographicsCardTypeLabel
     */
    public String getDemographicsCardTypeLabel() {
        return StringUtil.getLabelForView(demographicsCardTypeLabel);
    }

    /**
     *
     * @param demographicsCardTypeLabel The demographicsCardTypeLabel
     */
    public void setDemographicsCardTypeLabel(String demographicsCardTypeLabel) {
        this.demographicsCardTypeLabel = demographicsCardTypeLabel;
    }

    /**
     *
     * @return demographicsCardNumberLabel
     */
    public String getDemographicsCardNumberLabel() {
        return StringUtil.getLabelForView(demographicsCardNumberLabel);
    }

    /**
     *
     * @param demographicsCardNumberLabel The demographicsCardNumberLabel
     */
    public void setDemographicsCardNumberLabel(String demographicsCardNumberLabel) {
        this.demographicsCardNumberLabel = demographicsCardNumberLabel;
    }

    /**
     *
     * @return demographicsChangeNameLabel
     */
    public String getDemographicsChangeNameLabel() {
        return StringUtil.getLabelForView(demographicsChangeNameLabel);
    }

    /**
     *
     * @param demographicsChangeNameLabel The demographicsChangeNameLabel
     */
    public void setDemographicsChangeNameLabel(String demographicsChangeNameLabel) {
        this.demographicsChangeNameLabel = demographicsChangeNameLabel;
    }

    /**
     *
     * @return demographicsChangeEmailLabel
     */
    public String getDemographicsChangeEmailLabel() {
        return StringUtil.getLabelForView(demographicsChangeEmailLabel);
    }

    /**
     *
     * @param demographicsChangeEmailLabel demographicsChangeEmailLabel
     */
    public void setDemographicsChangeEmailLabel(String demographicsChangeEmailLabel) {
        this.demographicsChangeEmailLabel = demographicsChangeEmailLabel;
    }

}
