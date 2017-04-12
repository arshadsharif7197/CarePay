
package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsTransitionsDTO {

    @SerializedName("update_profile")
    @Expose
    private DemographicsSettingsUpdateProfileDTO demographicsSettingsUpdateProfileDTO = new DemographicsSettingsUpdateProfileDTO();
    @SerializedName("add_credit_card")
    @Expose
    private TransitionDTO demographicsSettingsAddCreditCardDTO = new TransitionDTO();
    @SerializedName("delete_credit_card")
    @Expose
    private TransitionDTO demographicsSettingsDeleteCreditCardDTO = new TransitionDTO();
    @SerializedName("update_documents")
    @Expose
    private TransitionDTO demographicsSettingsUpdateDocumentsDTO = new TransitionDTO();
    @SerializedName("update_demographics")
    @Expose
    private TransitionDTO demographicsSettingsUpdateDemographicsDTO = new TransitionDTO();
    @SerializedName("update_notifications")
    @Expose
    private TransitionDTO demographicsSettingsUpdateNotificationsDTO = new TransitionDTO();
    @SerializedName("update_credit_card")
    @Expose
    private TransitionDTO updateCreditCard = new TransitionDTO();
    @SerializedName("change_login_email")
    @Expose
    private TransitionDTO changeLoginEmail = new TransitionDTO();
    @SerializedName("change_password")
    @Expose
    private TransitionDTO changePassword = new TransitionDTO();


    public DemographicsSettingsUpdateProfileDTO getUpdateProfile() {
        return demographicsSettingsUpdateProfileDTO;
    }

    public void setUpdateProfile(DemographicsSettingsUpdateProfileDTO demographicsSettingsUpdateProfileDTO) {
        this.demographicsSettingsUpdateProfileDTO = demographicsSettingsUpdateProfileDTO;
    }

    public TransitionDTO getAddCreditCard() {
        return demographicsSettingsAddCreditCardDTO;
    }

    public void setAddCreditCard(TransitionDTO demographicsSettingsAddCreditCardDTO) {
        this.demographicsSettingsAddCreditCardDTO = demographicsSettingsAddCreditCardDTO;
    }

    public TransitionDTO getDeleteCreditCard() {
        return demographicsSettingsDeleteCreditCardDTO;
    }

    public void setDeleteCreditCard(TransitionDTO demographicsSettingsDeleteCreditCardDTO) {
        this.demographicsSettingsDeleteCreditCardDTO = demographicsSettingsDeleteCreditCardDTO;
    }

    public TransitionDTO getUpdateDocuments() {
        return demographicsSettingsUpdateDocumentsDTO;
    }

    public void setUpdateDocuments(TransitionDTO demographicsSettingsUpdateDocumentsDTO) {
        this.demographicsSettingsUpdateDocumentsDTO = demographicsSettingsUpdateDocumentsDTO;
    }

    public TransitionDTO getUpdateDemographics() {
        return demographicsSettingsUpdateDemographicsDTO;
    }

    public void setUpdateDemographics(TransitionDTO demographicsSettingsUpdateDemographicsDTO) {
        this.demographicsSettingsUpdateDemographicsDTO = demographicsSettingsUpdateDemographicsDTO;
    }

    public TransitionDTO getUpdateNotifications() {
        return demographicsSettingsUpdateNotificationsDTO;
    }

    public void setUpdateNotifications(TransitionDTO demographicsSettingsUpdateNotificationsDTO) {
        this.demographicsSettingsUpdateNotificationsDTO = demographicsSettingsUpdateNotificationsDTO;
    }

    public TransitionDTO getUpdateCreditCard() {
        return updateCreditCard;
    }

    public void setUpdateCreditCard(TransitionDTO updateCreditCard) {
        this.updateCreditCard = updateCreditCard;
    }

    public TransitionDTO getChangeLoginEmail() {
        return changeLoginEmail;
    }

    public void setChangeLoginEmail(TransitionDTO changeLoginEmail) {
        this.changeLoginEmail = changeLoginEmail;
    }

    public TransitionDTO getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(TransitionDTO changePassword) {
        this.changePassword = changePassword;
    }

}
