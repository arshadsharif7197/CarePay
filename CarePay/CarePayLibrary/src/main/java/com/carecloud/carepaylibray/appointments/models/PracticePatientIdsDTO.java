package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/31/2017.
 */

public class PracticePatientIdsDTO {

    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("practice_mgmt")
    @Expose
    private String practiceManagement;
    @SerializedName("patient_id")
    @Expose
    private String patientId;
    @SerializedName("practice_id")
    @Expose
    private String practiceId;

    /**
     *
     * @return prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     *
     * @param prefix prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     *
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     *
     * @param userId userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     *
     * @return patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     *
     * @param patientId patientId
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     *
     * @return practiceManagement
     */
    public String getPracticeManagement() {
        return practiceManagement;
    }

    /**
     *
     * @param practiceManagement practiceManagement
     */
    public void setPracticeManagement(String practiceManagement) {
        this.practiceManagement = practiceManagement;
    }

    /**
     *
     * @return practiceId
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     *
     * @param practiceId practiceId
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

}
