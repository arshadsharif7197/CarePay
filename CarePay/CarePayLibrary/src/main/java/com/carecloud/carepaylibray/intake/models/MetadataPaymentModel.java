package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetadataPaymentModel {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("patient_id")
    @Expose
    private String patientId;
    @SerializedName("created_dt")
    @Expose
    private String createdDt;
    @SerializedName("updated_dt")
    @Expose
    private String updatedDt;

    /**
     * 
     * @return
     *     The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 
     * @param userId
     *     The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 
     * @return
     *     The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * 
     * @param username
     *     The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return
     *     The patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * 
     * @param patientId
     *     The patient_id
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * 
     * @return
     *     The createdDt
     */
    public String getCreatedDt() {
        return createdDt;
    }

    /**
     * 
     * @param createdDt
     *     The created_dt
     */
    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }

    /**
     * 
     * @return
     *     The updatedDt
     */
    public String getUpdatedDt() {
        return updatedDt;
    }

    /**
     * 
     * @param updatedDt
     *     The updated_dt
     */
    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

}
