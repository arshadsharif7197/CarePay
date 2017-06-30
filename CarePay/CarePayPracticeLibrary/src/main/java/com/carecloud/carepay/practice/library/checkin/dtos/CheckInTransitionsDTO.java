package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class CheckInTransitionsDTO {
    @SerializedName("practice_appointments")
    @Expose
    private TransitionDTO practiceAppointments = new TransitionDTO();
    @SerializedName("cancel_appointment")
    @Expose
    private TransitionDTO cancelAppointment = new TransitionDTO();
    @SerializedName("checkin_appointment")
    @Expose
    private TransitionDTO checkinAppointment = new TransitionDTO();

    @SerializedName("confirm_appointment")
    @Expose
    private TransitionDTO confirmAppointment = new TransitionDTO();

    @SerializedName("make_appointment")
    @Expose
    private TransitionDTO makeAppointment = new TransitionDTO();

    @SerializedName("dismiss_appointment")
    @Expose
    private TransitionDTO dismissAppointment = new TransitionDTO();

    @SerializedName("checkin_patient_mode")
    @Expose
    private TransitionDTO checkinPatientMode = new TransitionDTO();

    @SerializedName("checkout_patient_mode")
    @Expose
    private TransitionDTO checkoutPatientMode = new TransitionDTO();

    @SerializedName("practice_mode")
    @Expose
    private TransitionDTO practiceMode = new TransitionDTO();

    /**
     * Gets dismiss appointment.
     *
     * @return the dismiss appointment
     */
    public TransitionDTO getDismissAppointment() {
        return dismissAppointment;
    }

    /**
     * Sets dismiss appointment.
     *
     * @param dismissAppointment the dismiss appointment
     */
    public void setDismissAppointment(TransitionDTO dismissAppointment) {
        this.dismissAppointment = dismissAppointment;
    }

    /**
     *
     * @return
     * The practiceAppointments
     */
    public TransitionDTO getPracticeAppointments() {
        return practiceAppointments;
    }

    /**
     *
     * @param practiceAppointments
     * The practice_appointments
     */
    public void setPracticeAppointments(TransitionDTO practiceAppointments) {
        this.practiceAppointments = practiceAppointments;
    }

    /**
     *
     * @return
     * The cancelAppointment
     */
    public TransitionDTO getCancelAppointment() {
        return cancelAppointment;
    }

    /**
     *
     * @param cancelAppointment
     * The cancel_appointment
     */
    public void setCancelAppointment(TransitionDTO cancelAppointment) {
        this.cancelAppointment = cancelAppointment;
    }

    /**
     *
     * @return
     * The checkinAppointment
     */
    public TransitionDTO getCheckinAppointment() {
        return checkinAppointment;
    }

    /**
     *
     * @param checkinAppointment
     * The checkin_appointment
     */
    public void setCheckinAppointment(TransitionDTO checkinAppointment) {
        this.checkinAppointment = checkinAppointment;
    }

    public TransitionDTO getConfirmAppointment() {
        return confirmAppointment;
    }

    public TransitionDTO getMakeAppointment() {
        return makeAppointment;
    }

    public TransitionDTO getCheckinPatientMode() {
        return checkinPatientMode;
    }

    public void setCheckinPatientMode(TransitionDTO checkinPatientMode) {
        this.checkinPatientMode = checkinPatientMode;
    }

    public TransitionDTO getPracticeMode() {
        return practiceMode;
    }

    public void setPracticeMode(TransitionDTO practiceMode) {
        this.practiceMode = practiceMode;
    }

    public TransitionDTO getCheckoutPatientMode() {
        return checkoutPatientMode;
    }

    public void setCheckoutPatientMode(TransitionDTO checkoutPatientMode) {
        this.checkoutPatientMode = checkoutPatientMode;
    }
}
