package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransitionsModel {

    @SerializedName("confirm_payment")
    @Expose
    private ConfirmPaymentModel confirmPaymentModel;
    @SerializedName("transition_appointments")
    @Expose
    private TransitionAppointmentsModel transitionAppointments;
    @SerializedName("checkin_appointment")
    @Expose
    private CheckInAppointmentModel checkInAppointmentModel;
    @SerializedName("update_payment")
    @Expose
    private UpdatePaymentModel updatePayment;

    /**
     * 
     * @return
     *     The confirmPaymentModel
     */
    public ConfirmPaymentModel getConfirmPaymentModel() {
        return confirmPaymentModel;
    }

    /**
     * 
     * @param confirmPaymentModel
     *     The confirm_payment
     */
    public void setConfirmPaymentModel(ConfirmPaymentModel confirmPaymentModel) {
        this.confirmPaymentModel = confirmPaymentModel;
    }

    /**
     * 
     * @return
     *     The transitionAppointments
     */
    public TransitionAppointmentsModel getTransitionAppointments() {
        return transitionAppointments;
    }

    /**
     * 
     * @param transitionAppointments
     *     The transition_appointments
     */
    public void setTransitionAppointments(TransitionAppointmentsModel transitionAppointments) {
        this.transitionAppointments = transitionAppointments;
    }

    /**
     * 
     * @return
     *     The checkInAppointmentModel
     */
    public CheckInAppointmentModel getCheckInAppointmentModel() {
        return checkInAppointmentModel;
    }

    /**
     * 
     * @param checkInAppointmentModel
     *     The checkin_appointment
     */
    public void setCheckInAppointmentModel(CheckInAppointmentModel checkInAppointmentModel) {
        this.checkInAppointmentModel = checkInAppointmentModel;
    }

    /**
     * 
     * @return
     *     The updatePayment
     */
    public UpdatePaymentModel getUpdatePayment() {
        return updatePayment;
    }

    /**
     * 
     * @param updatePayment
     *     The update_payment
     */
    public void setUpdatePayment(UpdatePaymentModel updatePayment) {
        this.updatePayment = updatePayment;
    }

}
