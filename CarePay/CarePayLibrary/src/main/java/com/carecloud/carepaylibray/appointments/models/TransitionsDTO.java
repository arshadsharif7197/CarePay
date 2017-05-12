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

    @SerializedName("make_appointment")
    @Expose
    private TransitionDTO makeAppointment = new TransitionDTO();

    @SerializedName("delete_notifications")
    @Expose
    private TransitionDTO deleteNotifications = new TransitionDTO();

    @SerializedName("mark_as_read")
    @Expose
    private TransitionDTO readNotifications = new TransitionDTO();

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
     *
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

    public TransitionDTO getReadNotifications() {
        return readNotifications;
    }

    public void setReadNotifications(TransitionDTO readNotifications) {
        this.readNotifications = readNotifications;
    }
}
