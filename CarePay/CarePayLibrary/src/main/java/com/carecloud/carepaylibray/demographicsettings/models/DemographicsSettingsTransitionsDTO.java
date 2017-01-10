
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
    private DemographicsSettingsAddCreditCardDTO demographicsSettingsAddCreditCardDTO;
    @SerializedName("delete_credit_card")
    @Expose
    private DemographicsSettingsDeleteCreditCardDTO demographicsSettingsDeleteCreditCardDTO;
    @SerializedName("update_documents")
    @Expose
    private DemographicsSettingsUpdateDocumentsDTO demographicsSettingsUpdateDocumentsDTO;
    @SerializedName("update_demographics")
    @Expose
    private TransitionDTO demographicsSettingsUpdateDemographicsDTO;
    @SerializedName("update_notifications")
    @Expose
    private DemographicsSettingsUpdateNotificationsDTO demographicsSettingsUpdateNotificationsDTO;

    public DemographicsSettingsUpdateProfileDTO getUpdateProfile() {
        return demographicsSettingsUpdateProfileDTO;
    }

    public void setUpdateProfile(DemographicsSettingsUpdateProfileDTO demographicsSettingsUpdateProfileDTO) {
        this.demographicsSettingsUpdateProfileDTO = demographicsSettingsUpdateProfileDTO;
    }

    public DemographicsSettingsAddCreditCardDTO getAddCreditCard() {
        return demographicsSettingsAddCreditCardDTO;
    }

    public void setAddCreditCard(DemographicsSettingsAddCreditCardDTO demographicsSettingsAddCreditCardDTO) {
        this.demographicsSettingsAddCreditCardDTO = demographicsSettingsAddCreditCardDTO;
    }

    public DemographicsSettingsDeleteCreditCardDTO getDeleteCreditCard() {
        return demographicsSettingsDeleteCreditCardDTO;
    }

    public void setDeleteCreditCard(DemographicsSettingsDeleteCreditCardDTO demographicsSettingsDeleteCreditCardDTO) {
        this.demographicsSettingsDeleteCreditCardDTO = demographicsSettingsDeleteCreditCardDTO;
    }

    public DemographicsSettingsUpdateDocumentsDTO getUpdateDocuments() {
        return demographicsSettingsUpdateDocumentsDTO;
    }

    public void setUpdateDocuments(DemographicsSettingsUpdateDocumentsDTO demographicsSettingsUpdateDocumentsDTO) {
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

}
