package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Scan QR Code
 */
public class ScanQRCodeDTO {

    @SerializedName("metadata")
    @Expose
    private AppointmentMetadataModel metadata = new AppointmentMetadataModel();
    @SerializedName("payload")
    @Expose
    private QRCodePayloadDTO payload = new QRCodePayloadDTO();
    @SerializedName("state")
    @Expose
    private String state;

    /**
     * 
     * @return
     *     The metadata
     */
    public AppointmentMetadataModel getMetadata() {
        return metadata;
    }

    /**
     * 
     * @param metadata
     *     The metadata
     */
    public void setMetadata(AppointmentMetadataModel metadata) {
        this.metadata = metadata;
    }

    /**
     * 
     * @return
     *     The payload
     */
    public QRCodePayloadDTO getPayload() {
        return payload;
    }

    /**
     * 
     * @param payload
     *     The payload
     */
    public void setPayload(QRCodePayloadDTO payload) {
        this.payload = payload;
    }

    /**
     * 
     * @return
     *     The state
     */
    public String getState() {
        return state;
    }

    /**
     * 
     * @param state
     *     The state
     */
    public void setState(String state) {
        this.state = state;
    }

}
