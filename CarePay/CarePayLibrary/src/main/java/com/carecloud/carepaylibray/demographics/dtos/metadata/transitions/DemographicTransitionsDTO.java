package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Model for demographics transition.
 */
public class DemographicTransitionsDTO {
    @SerializedName("confirm_demographics")
    @Expose
    private TransitionDTO confirmDemographics = new TransitionDTO();

    @SerializedName("update_demographics")
    @Expose
    private TransitionDTO updateDemographics = new TransitionDTO();

    @SerializedName("add_credit_card")
    @Expose
    private TransitionDTO addCreditCard = new TransitionDTO();

    @SerializedName("delete_credit_card")
    @Expose
    private TransitionDTO deleteCreditCard = new TransitionDTO();

    @SerializedName("update_documents")
    @Expose
    private TransitionDTO udateDocuments = new TransitionDTO();

    @SerializedName("update_notifications")
    @Expose
    private TransitionDTO updateNotifications = new TransitionDTO();

    @SerializedName("update_credit_card")
    @Expose
    private TransitionDTO updateCreditCard = new TransitionDTO();

    @SerializedName("change_login_email")
    @Expose
    private TransitionDTO changeLoginEmail = new TransitionDTO();

    @SerializedName("change_password")
    @Expose
    private TransitionDTO changePassword = new TransitionDTO();

    @SerializedName("delegate_action")
    @Expose
    private TransitionDTO delegateAction;

    @SerializedName("update_permissions")
    @Expose
    private TransitionDTO updatePermissions;

    /**
     *
     * @return
     * The confirmDemographics
     */
    public TransitionDTO getConfirmDemographics() {
        return confirmDemographics;
    }

    /**
     *
     * @param confirmDemographics
     * The confirm_demographics
     */
    public void setConfirmDemographics(TransitionDTO confirmDemographics) {
        this.confirmDemographics = confirmDemographics;
    }


    /**
     * @return The updateDemographics
     */
    public TransitionDTO getUpdateDemographics() {
        return updateDemographics;
    }

    /**
     * @param updateDemographics The update_demographics
     */
    public void setUpdateDemographics(TransitionDTO updateDemographics) {
        this.updateDemographics = updateDemographics;
    }

    public TransitionDTO getAddCreditCard() {
        return addCreditCard;
    }

    public void setAddCreditCard(TransitionDTO addCreditCard) {
        this.addCreditCard = addCreditCard;
    }

    public TransitionDTO getDeleteCreditCard() {
        return deleteCreditCard;
    }

    public void setDeleteCreditCard(TransitionDTO deleteCreditCard) {
        this.deleteCreditCard = deleteCreditCard;
    }

    public TransitionDTO getUdateDocuments() {
        return udateDocuments;
    }

    public void setUdateDocuments(TransitionDTO udateDocuments) {
        this.udateDocuments = udateDocuments;
    }

    public TransitionDTO getUpdateNotifications() {
        return updateNotifications;
    }

    public void setUpdateNotifications(TransitionDTO updateNotifications) {
        this.updateNotifications = updateNotifications;
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

    public TransitionDTO getAction() {
        return delegateAction;
    }

    public void setAction(TransitionDTO delegateAction) {
        this.delegateAction = delegateAction;
    }

    public TransitionDTO getUpdatePermissions() {
        return updatePermissions;
    }

    public void setUpdatePermissions(TransitionDTO updatePermissions) {
        this.updatePermissions = updatePermissions;
    }
}
