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
    private QRCodeDTO qrcode;

    /**
     * 
     * @return
     *     The qrcode
     */
    public QRCodeDTO getQrcode() {
        return qrcode;
    }

    /**
     * 
     * @param qrcode
     *     The qrcode
     */
    public void setQrcode(QRCodeDTO qrcode) {
        this.qrcode = qrcode;
    }

}
