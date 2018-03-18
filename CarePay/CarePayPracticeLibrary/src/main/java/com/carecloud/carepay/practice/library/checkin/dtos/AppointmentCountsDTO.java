package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class AppointmentCountsDTO {
    @SerializedName("Pending")
    @Expose
    private Integer pending;
    @SerializedName("Checked-In")
    @Expose
    private Integer checkedIn;
    @SerializedName("Checked-Out")
    @Expose
    private Integer checkedOut;
    @SerializedName("Cancelled")
    @Expose
    private Integer cancelled;
    @SerializedName("Requested")
    @Expose
    private Integer requested;
    @SerializedName("Checking-In")
    @Expose
    private Integer checkingIn;
    @SerializedName("Checking-Out")
    @Expose
    private Integer checkingOut;

    public Integer getCancelled() {
        return cancelled;
    }

    public void setCancelled(Integer cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * @return The requested
     */
    public Integer getRequested() {
        return requested;
    }

    /**
     * @param requested The Requested
     */
    public void setRequested(Integer requested) {
        this.requested = requested;
    }

    /**
     * @return The checkingIn
     */
    public Integer getCheckingIn() {
        return checkingIn;
    }

    /**
     * @param checkingIn The checkingIn
     */
    public void setCheckingIn(Integer checkingIn) {
        this.checkingIn = checkingIn;
    }

    /**
     * @return The pending
     */
    public Integer getPending() {
        return pending;
    }

    /**
     * @param pending The Pending
     */
    public void setPending(Integer pending) {
        this.pending = pending;
    }

    /**
     * @return The checkedIn
     */
    public Integer getCheckedIn() {
        return checkedIn;
    }

    /**
     * @param checkedIn The Checked-In
     */
    public void setCheckedIn(Integer checkedIn) {
        this.checkedIn = checkedIn;
    }

    public Integer getCheckedOut() {
        return checkedOut;
    }

    public void setCheckedOut(Integer checkedOut) {
        this.checkedOut = checkedOut;
    }

    public Integer getCheckingOut() {
        return checkingOut;
    }

    public void setCheckingOut(Integer checkingOut) {
        this.checkingOut = checkingOut;
    }
}
