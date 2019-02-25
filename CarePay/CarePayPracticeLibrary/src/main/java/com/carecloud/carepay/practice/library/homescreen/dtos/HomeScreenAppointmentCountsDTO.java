package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenAppointmentCountsDTO {
    @SerializedName("Pending")
    @Expose
    private int pendingCount;
    @SerializedName("Checked-In")
    @Expose
    private int checkedInCount;

    @SerializedName("Cancelled")
    @Expose
    private int cancelledCount;
    @SerializedName("Requested")
    @Expose
    private int requestedCount;
    @SerializedName("Checking-In")
    @Expose
    private int checkingInCount;



    /**
     *
     * @return
     * The pendingCount
     */
    public int getPendingCount() {
        return pendingCount;
    }

    /**
     *
     * @param pendingCount
     * The Pending
     */
    public void setPendingCount(int pendingCount) {
        this.pendingCount = pendingCount;
    }

    /**
     *
     * @return
     * The checkedInCount
     */
    public int getCheckedInCount() {
        return checkedInCount;
    }

    /**
     *
     * @param checkedInCount
     * The Checked-In
     */
    public void setCheckedInCount(int checkedInCount) {
        this.checkedInCount = checkedInCount;
    }

    public int getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(int cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public int getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(int requestedCount) {
        this.requestedCount = requestedCount;
    }

    public int getCheckingInCount() {
        return checkingInCount;
    }

    public void setCheckingInCount(int checkingInCount) {
        this.checkingInCount = checkingInCount;
    }
}
