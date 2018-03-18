package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/17/2016.
 * Model for Scan QR Code Result.
 */

public class ScanQRCodeResultDTO {

    @SerializedName("practice_mgmt")
    @Expose
    private String practiceManagement;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;
    @SerializedName("appointment_id")
    @Expose
    private String appointmentId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("user_id")
    @Expose
    private String userId;

    /**
     *
     * @return
     * The practiceManagement
     */
    public String getPracticeManagement() {
        return practiceManagement;
    }

    /**
     *
     * @param practiceManagement
     * The practiceManagement
     */
    public void setPracticeManagement(String practiceManagement) {
        this.practiceManagement = practiceManagement;
    }

    /**
     *
     * @return
     * The practiceId
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     *
     * @param practiceId
     * The practiceId
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    /**
     *
     * @return
     * The appointmentId
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     *
     * @param appointmentId
     * The appointmentId
     */
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     *
     * @return
     * The username
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     * The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     * The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId
     * The userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
