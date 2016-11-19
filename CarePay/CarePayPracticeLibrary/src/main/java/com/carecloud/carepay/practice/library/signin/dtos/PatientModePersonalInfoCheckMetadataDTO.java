package com.carecloud.carepay.practice.library.signin.dtos;

/**
 * Created by kavin kannan on 11/18/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModePersonalInfoCheckMetadataDTO {

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
     * The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
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
     * The practiceMgmt
     */
    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     *
     * @param practiceMgmt
     * The practice_mgmt
     */
    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
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
     * The practice_id
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    /**
     *
     * @return
     * The patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     *
     * @param patientId
     * The patient_id
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

}
