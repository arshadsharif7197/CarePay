package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Transitions
 */
public class TransitionsDTO {

    @SerializedName("cancel") @Expose
    private TransitionDTO cancel;

    @SerializedName("checkin") @Expose
    private TransitionDTO checkin;

    @SerializedName("checkin_at_office") @Expose
    private TransitionDTO checkinAtOffice;

    @SerializedName("add") @Expose
    private TransitionDTO add;

    @SerializedName("logout") @Expose
    private TransitionDTO logout;

    @SerializedName("checking_in") @Expose
    private TransitionDTO checking_in;

    /**
     * @return The checking_in
     */
    public TransitionDTO getChecking_in() {
        return checking_in;
    }

    /**
     * @param checking_in The checking_in
     */
    public void setChecking_in(TransitionDTO checking_in) {
        this.checking_in = checking_in;
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
     * @return The checkin
     */
    public TransitionDTO getCheckin() {
        return checkin;
    }

    /**
     * @param checkin The checkin
     */
    public void setCheckin(TransitionDTO checkin) {
        this.checkin = checkin;
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
}
