package com.carecloud.carepaylibray.adhoc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/10/17.
 */

public class AdhocFormsPatientModeInfoMetadata {

    @Expose
    @SerializedName("user_id")
    private String userId;
    @Expose
    @SerializedName("username")
    private String username;
    @Expose
    @SerializedName("practice_mgmt")
    private String practiceMgmt;
    @Expose
    @SerializedName("practice_id")
    private String practiceId;
    @Expose
    @SerializedName("patient_id")
    private String patientId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
