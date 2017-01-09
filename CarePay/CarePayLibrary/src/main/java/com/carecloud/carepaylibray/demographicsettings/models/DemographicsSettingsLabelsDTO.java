
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

}
