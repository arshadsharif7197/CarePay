package com.carecloud.carepaylibray.base.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 9/08/17.
 */

public class LandingLinks extends BaseLinks {

    @SerializedName("patient_balances")
    @Expose
    private TransitionDTO patientBalances = new TransitionDTO();
    @SerializedName("profile_update")
    @Expose
    private TransitionDTO profileUpdate = new TransitionDTO();
    @SerializedName("appointments")
    @Expose
    private TransitionDTO appointments = new TransitionDTO();
    @SerializedName("notifications")
    @Expose
    private TransitionDTO notifications = new TransitionDTO();
    @SerializedName("myhealth")
    @Expose
    private TransitionDTO myHealth;
    @SerializedName("unread_notifications")
    @Expose
    private TransitionDTO unreadNotifications = new TransitionDTO();
    @SerializedName("retail")
    @Expose
    private TransitionDTO retail = new TransitionDTO();

    public TransitionDTO getPatientBalances() {
        return patientBalances;
    }

    public void setPatientBalances(TransitionDTO patientBalances) {
        this.patientBalances = patientBalances;
    }

    public TransitionDTO getProfileUpdate() {
        return profileUpdate;
    }

    public void setProfileUpdate(TransitionDTO profileUpdate) {
        this.profileUpdate = profileUpdate;
    }

    public TransitionDTO getAppointments() {
        return appointments;
    }

    public void setAppointments(TransitionDTO appointments) {
        this.appointments = appointments;
    }

    public TransitionDTO getNotifications() {
        return notifications;
    }

    public void setNotifications(TransitionDTO notifications) {
        this.notifications = notifications;
    }

    public TransitionDTO getMyHealth() {
        return myHealth;
    }

    public void setMyHealth(TransitionDTO myHealth) {
        this.myHealth = myHealth;
    }

    public TransitionDTO getUnreadNotifications() {
        return unreadNotifications;
    }

    public void setUnreadNotifications(TransitionDTO unreadNotifications) {
        this.unreadNotifications = unreadNotifications;
    }

    public TransitionDTO getRetail() {
        return retail;
    }

    public void setRetail(TransitionDTO retail) {
        this.retail = retail;
    }
}
