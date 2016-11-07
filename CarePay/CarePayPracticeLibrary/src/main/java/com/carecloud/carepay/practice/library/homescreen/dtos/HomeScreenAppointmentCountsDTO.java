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

}
