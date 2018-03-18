package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenAppointmentCountsDTO {
    @SerializedName("Pending")
    @Expose
    private Integer pendingCount;
    @SerializedName("Checked-In")
    @Expose
    private Integer checkedInCount;

    @SerializedName("Cancelled")
    @Expose
    private Integer cancelledCount;
    @SerializedName("Requested")
    @Expose
    private Integer requestedCount;
    @SerializedName("Checking-In")
    @Expose
    private Integer checkingInCount;



    /**
     *
     * @return
     * The pendingCount
     */
    public Integer getPendingCount() {
        return pendingCount ==null?0: pendingCount;
    }

    /**
     *
     * @param pendingCount
     * The Pending
     */
    public void setPendingCount(Integer pendingCount) {
        this.pendingCount = pendingCount;
    }

    /**
     *
     * @return
     * The checkedInCount
     */
    public Integer getCheckedInCount() {
        return checkedInCount ==null?0: checkedInCount;
    }

    /**
     *
     * @param checkedInCount
     * The Checked-In
     */
    public void setCheckedInCount(Integer checkedInCount) {
        this.checkedInCount = checkedInCount;
    }

    public Integer getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(Integer cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public Integer getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(Integer requestedCount) {
        this.requestedCount = requestedCount;
    }

    public Integer getCheckingInCount() {
        return checkingInCount;
    }

    public void setCheckingInCount(Integer checkingInCount) {
        this.checkingInCount = checkingInCount;
    }
}
