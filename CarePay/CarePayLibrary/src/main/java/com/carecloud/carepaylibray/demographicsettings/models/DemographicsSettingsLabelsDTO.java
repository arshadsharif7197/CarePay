
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
}
