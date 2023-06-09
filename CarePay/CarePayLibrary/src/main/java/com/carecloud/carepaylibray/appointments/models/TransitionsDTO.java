package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Transitions
 */
public class TransitionsDTO {

    @SerializedName("cancel")
    @Expose
    private TransitionDTO cancel = new TransitionDTO();

    @SerializedName("checkin_at_office")
    @Expose
    private TransitionDTO checkinAtOffice = new TransitionDTO();

    @SerializedName("add")
    @Expose
    private TransitionDTO add = new TransitionDTO();

    @SerializedName("logout")
    @Expose
    private TransitionDTO logout = new TransitionDTO();

    @SerializedName("checking_in")
    @Expose
    private TransitionDTO checkingIn = new TransitionDTO();

    @SerializedName("checking_out")
    @Expose
    private TransitionDTO checkingOut = new TransitionDTO();

    @SerializedName("make_appointment")
    @Expose
    private TransitionDTO makeAppointment = new TransitionDTO();

    @SerializedName("delete_notifications")
    @Expose
    private TransitionDTO deleteNotifications = new TransitionDTO();

    @SerializedName("delete_all_notifications")
    @Expose
    private TransitionDTO deleteAllNotifications = new TransitionDTO();

    @SerializedName("mark_as_read")
    @Expose
    private TransitionDTO readNotifications = new TransitionDTO();

    @SerializedName("continue")
    @Expose
    private TransitionDTO continueTransition = new TransitionDTO();

    @SerializedName("update_forms")
    @Expose
    private TransitionDTO updateForms = new TransitionDTO();

    @SerializedName("update_pending_forms")
    @Expose
    private TransitionDTO updatePendingForms = new TransitionDTO();

    @SerializedName("practice_mode")
    @Expose
    private TransitionDTO practiceMode = new TransitionDTO();

    @SerializedName("adhoc_forms_patient_mode")
    @Expose
    private TransitionDTO adHocFormsPatientMode = new TransitionDTO();

    @SerializedName("make_payment")
    @Expose
    private TransitionDTO makePayment = new TransitionDTO();
    @SerializedName("add_credit_card")
    @Expose
    private TransitionDTO addCreditCard = new TransitionDTO();
    @SerializedName("save_survey_response")
    @Expose
    private TransitionDTO saveSurvey = new TransitionDTO();
    @SerializedName("confirm_appointment")
    @Expose
    private TransitionDTO confirmAppointment = new TransitionDTO();
    @SerializedName("checkin_patient_mode")
    @Expose
    private TransitionDTO checkinPatientMode = new TransitionDTO();
    @SerializedName("checkout_patient_mode")
    @Expose
    private TransitionDTO checkoutPatientMode = new TransitionDTO();
    @SerializedName("cancel_appointment")
    @Expose
    private TransitionDTO cancelAppointment = new TransitionDTO();
    @SerializedName("dismiss_appointment")
    @Expose
    private TransitionDTO dismissAppointment = new TransitionDTO();

    /**
     * @return The checkingIn
     */
    public TransitionDTO getCheckingIn() {
        return checkingIn;
    }

    /**
     * @param checkingIn The checking_in
     */
    public void setCheckingIn(TransitionDTO checkingIn) {
        this.checkingIn = checkingIn;
    }

    /**
     * @return The checkingOut
     */
    public TransitionDTO getCheckingOut() {
        return checkingOut;
    }

    /**
     * @param checkingOut The checking_out
     */
    public void setCheckingOut(TransitionDTO checkingOut) {
        this.checkingOut = checkingOut;
    }

    /**
     * @return The cancel
     */
    public TransitionDTO getCancel() {
        return cancel;
    }

    /**
     * @param cancel The cancel
     */
    public void setCancel(TransitionDTO cancel) {
        this.cancel = cancel;
    }

    /**
     * @return The checkinAtOffice
     */
    public TransitionDTO getCheckinAtOffice() {
        return checkinAtOffice;
    }

    /**
     * @param checkinAtOffice The checkin_at_office
     */
    public void setCheckinAtOffice(TransitionDTO checkinAtOffice) {
        this.checkinAtOffice = checkinAtOffice;
    }

    /**
     * @return The add
     */
    public TransitionDTO getAdd() {
        return add;
    }

    /**
     * @param add The add
     */
    public void setAdd(TransitionDTO add) {
        this.add = add;
    }

    public TransitionDTO getLogout() {
        return logout;
    }

    public void setLogout(TransitionDTO logout) {
        this.logout = logout;
    }

    /**
     * Gets make appointment.
     *
     * @return the make appointment
     */
    public TransitionDTO getMakeAppointment() {
        return makeAppointment;
    }

    /**
     * Sets make appointment.
     *
     * @param makeAppointment the make appointment
     */
    public void setMakeAppointment(TransitionDTO makeAppointment) {
        this.makeAppointment = makeAppointment;
    }

    public TransitionDTO getDeleteNotifications() {
        return deleteNotifications;
    }

    public void setDeleteNotifications(TransitionDTO deleteNotifications) {
        this.deleteNotifications = deleteNotifications;
    }

    public TransitionDTO getDeleteAllNotifications() {
        return deleteAllNotifications;
    }

    public void setDeleteAllNotifications(TransitionDTO deleteAllNotifications) {
        this.deleteAllNotifications = deleteAllNotifications;
    }

    public TransitionDTO getReadNotifications() {
        return readNotifications;
    }

    public void setReadNotifications(TransitionDTO readNotifications) {
        this.readNotifications = readNotifications;
    }

    public TransitionDTO getContinueTransition() {
        return continueTransition;
    }

    public void setContinueTransition(TransitionDTO continueTransition) {
        this.continueTransition = continueTransition;
    }

    public TransitionDTO getUpdateForms() {
        return updateForms;
    }

    public void setUpdateForms(TransitionDTO updateForms) {
        this.updateForms = updateForms;
    }

    public TransitionDTO getPracticeMode() {
        return practiceMode;
    }

    public void setPracticeMode(TransitionDTO practiceMode) {
        this.practiceMode = practiceMode;
    }

    public TransitionDTO getAdHocFormsPatientMode() {
        return adHocFormsPatientMode;
    }

    public void setAdHocFormsPatientMode(TransitionDTO adHocFormsPatientMode) {
        this.adHocFormsPatientMode = adHocFormsPatientMode;
    }

    public TransitionDTO getMakePayment() {
        return makePayment;
    }

    public void setMakePayment(TransitionDTO makePayment) {
        this.makePayment = makePayment;
    }

    public TransitionDTO getAddCreditCard() {
        return addCreditCard;
    }

    public void setAddCreditCard(TransitionDTO addCreditCard) {
        this.addCreditCard = addCreditCard;
    }

    public TransitionDTO getUpdatePendingForms() {
        return updatePendingForms;
    }

    public void setUpdatePendingForms(TransitionDTO updatePendingForms) {
        this.updatePendingForms = updatePendingForms;
    }

    public TransitionDTO getSaveSurvey() {
        return saveSurvey;
    }

    public void setSaveSurvey(TransitionDTO saveSurvey) {
        this.saveSurvey = saveSurvey;
    }

    public TransitionDTO getConfirmAppointment() {
        return confirmAppointment;
    }

    public void setConfirmAppointment(TransitionDTO confirmAppointment) {
        this.confirmAppointment = confirmAppointment;
    }

    public TransitionDTO getCheckinPatientMode() {
        return checkinPatientMode;
    }

    public void setCheckinPatientMode(TransitionDTO checkinPatientMode) {
        this.checkinPatientMode = checkinPatientMode;
    }

    public TransitionDTO getCheckoutPatientMode() {
        return checkoutPatientMode;
    }

    public void setCheckoutPatientMode(TransitionDTO checkoutPatientMode) {
        this.checkoutPatientMode = checkoutPatientMode;
    }

    public TransitionDTO getCancelAppointment() {
        return cancelAppointment;
    }

    public void setCancelAppointment(TransitionDTO cancelAppointment) {
        this.cancelAppointment = cancelAppointment;
    }

    public TransitionDTO getDismissAppointment() {
        return dismissAppointment;
    }

    public void setDismissAppointment(TransitionDTO dismissAppointment) {
        this.dismissAppointment = dismissAppointment;
    }
}
