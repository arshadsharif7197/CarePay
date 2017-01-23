
package com.carecloud.carepaylibray.demographicsettings.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsTransitionsDTO {

    @SerializedName("update_profile")
    @Expose
    private DemographicsSettingsUpdateProfileDTO demographicsSettingsUpdateProfileDTO;
    @SerializedName("add_credit_card")
    @Expose
    private TransitionDTO demographicsSettingsAddCreditCardDTO;
    @SerializedName("delete_credit_card")
    @Expose
    private TransitionDTO demographicsSettingsDeleteCreditCardDTO;
    @SerializedName("update_documents")
    @Expose
    private TransitionDTO demographicsSettingsUpdateDocumentsDTO;
    @SerializedName("update_demographics")
    @Expose
    private TransitionDTO demographicsSettingsUpdateDemographicsDTO;
    @SerializedName("update_notifications")
    @Expose
    private DemographicsSettingsUpdateNotificationsDTO demographicsSettingsUpdateNotificationsDTO;
    @SerializedName("update_credit_card")
    @Expose
    private TransitionDTO updateCreditCard;

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

    public DemographicsSettingsUpdateNotificationsDTO getUpdateNotifications() {
        return demographicsSettingsUpdateNotificationsDTO;
    }

    public void setUpdateNotifications(DemographicsSettingsUpdateNotificationsDTO demographicsSettingsUpdateNotificationsDTO) {
        this.demographicsSettingsUpdateNotificationsDTO = demographicsSettingsUpdateNotificationsDTO;
    }

    public TransitionDTO getUpdateCreditCard() {
        return updateCreditCard;
    }

    public void setUpdateCreditCard(TransitionDTO updateCreditCard) {
        this.updateCreditCard = updateCreditCard;
    }
}
