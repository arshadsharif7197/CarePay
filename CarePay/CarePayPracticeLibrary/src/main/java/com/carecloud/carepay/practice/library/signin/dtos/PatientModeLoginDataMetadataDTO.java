package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by sudhir_pingale on 11/18/2016.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeLoginDataMetadataDTO {
    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;
    @SerializedName("practice_id")

    @Expose
    private String practiceId;

    @SerializedName("patient_id")
    @Expose
    private String patientId;

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets practice mgmt.
     *
     * @return the practice mgmt
     */
    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     * Sets practice mgmt.
     *
     * @param practiceMgmt the practice mgmt
     */
    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     * Gets practice id.
     *
     * @return the practice id
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     * Sets practice id.
     *
     * @param practiceId the practice id
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    /**
     * Gets patient id.
     *
     * @return the patient id
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * Sets patient id.
     *
     * @param patientId the patient id
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
