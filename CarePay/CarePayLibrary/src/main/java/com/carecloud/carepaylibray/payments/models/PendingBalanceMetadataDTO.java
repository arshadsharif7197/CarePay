package com.carecloud.carepaylibray.payments.models;

/**
 * Created by Rahul on 11/30/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PendingBalanceMetadataDTO implements Serializable {
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
     * @return The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The practiceMgmt
     */
    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     * @param practiceMgmt The practice_mgmt
     */
    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     * @return The practiceId
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     * @param practiceId The practice_id
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    /**
     * @return The patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId The patient_id
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * validate this metadata
     * @return true if required fields are not null
     */
    public boolean isValid(){
        return userId != null &&
                username != null &&
                practiceMgmt != null &&
                practiceId != null &&
                patientId != null;
    }

}