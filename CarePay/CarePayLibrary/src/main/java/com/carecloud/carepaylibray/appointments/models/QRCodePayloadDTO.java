package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for QR Code Payload
 */
public class QRCodePayloadDTO {

    @SerializedName("qrcode")
    @Expose
    private String qrCode;

    /**
     *
     * @return
     *     The qrCode
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     *
     * @param qrCode
     *     The qrCode
     */
    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
