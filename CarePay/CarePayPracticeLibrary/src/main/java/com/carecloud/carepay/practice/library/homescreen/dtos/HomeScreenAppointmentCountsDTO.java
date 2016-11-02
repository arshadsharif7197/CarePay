package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenAppointmentCountsDTO {
    @SerializedName("Pending")
    @Expose
    private Integer pending;
    @SerializedName("Checked-In")
    @Expose
    private Integer checkedIn;

    /**
     *
     * @return
     * The pending
     */
    public Integer getPending() {
        return pending;
    }

    /**
     *
     * @param pending
     * The Pending
     */
    public void setPending(Integer pending) {
        this.pending = pending;
    }

    /**
     *
     * @return
     * The checkedIn
     */
    public Integer getCheckedIn() {
        return checkedIn;
    }

    /**
     *
     * @param checkedIn
     * The Checked-In
     */
    public void setCheckedIn(Integer checkedIn) {
        this.checkedIn = checkedIn;
    }

}
