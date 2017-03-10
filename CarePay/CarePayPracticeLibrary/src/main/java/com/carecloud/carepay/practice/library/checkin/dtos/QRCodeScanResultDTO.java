package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 11/17/2016.
 * QR scan result
 */

public class QRCodeScanResultDTO extends UserPracticeDTO {
    @SerializedName("appointment_id")
    @Expose
    private String appointmentId;

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
