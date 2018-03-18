package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhir_pingale on 11/11/2016.
 */

public class CheckInStatusPayloadDTO {
    @SerializedName("checkin_status")
    @Expose
    private CheckInStatusDataPayloadDTO checkinStatus = new CheckInStatusDataPayloadDTO();

    /**
     * @return The checkinStatus
     */
    public CheckInStatusDataPayloadDTO getCheckInStatusData() {
        return checkinStatus;
    }

    /**
     * @param checkinStatus The checkin_status
     */
    public void setCheckInStatusData(CheckInStatusDataPayloadDTO checkinStatus) {
        this.checkinStatus = checkinStatus;
    }
}
