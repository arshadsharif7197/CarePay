package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sudhir_pingale on 12/20/2016.
 */

public class AppointmentAvailabilityMetadataDTO implements Serializable {

    @SerializedName("resource_ids")
    @Expose
    private String resourceIds;

    @SerializedName("visit_reason_id")
    @Expose
    private String visitReasonId;

    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;

    @SerializedName("end_date")
    @Expose
    private String endDate;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("location_ids")
    @Expose
    private String locationIds;

    @SerializedName("user_id")
    @Expose
    private String userId;

    @SerializedName("patient_id")
    @Expose
    private String patientId;

    @SerializedName("updated_dt")
    @Expose
    private String updatedDt;

    @SerializedName("created_dt")
    @Expose
    private String createdDt;

    @SerializedName("start_date")
    @Expose
    private String startDate;

    @SerializedName("practice_id")
    @Expose
    private String practiceId;

    /**
     * Gets resource ids.
     *
     * @return the resource ids
     */
    public String getResourceIds() {
        return resourceIds;
    }

    /**
     * Sets resource ids.
     *
     * @param resourceIds the resource ids
     */
    public void setResourceIds(String resourceIds) {
        this.resourceIds = resourceIds;
    }

    /**
     * Gets visit reason id.
     *
     * @return the visit reason id
     */
    public String getVisitReasonId() {
        return visitReasonId;
    }

    /**
     * Sets visit reason id.
     *
     * @param visitReasonId the visit reason id
     */
    public void setVisitReasonId(String visitReasonId) {
        this.visitReasonId = visitReasonId;
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
     * Gets end date.
     *
     * @return the end date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets end date.
     *
     * @param endDate the end date
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
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
     * Gets location ids.
     *
     * @return the location ids
     */
    public String getLocationIds() {
        return locationIds;
    }

    /**
     * Sets location ids.
     *
     * @param locationIds the location ids
     */
    public void setLocationIds(String locationIds) {
        this.locationIds = locationIds;
    }

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

    /**
     * Gets updated dt.
     *
     * @return the updated dt
     */
    public String getUpdatedDt() {
        return updatedDt;
    }

    /**
     * Sets updated dt.
     *
     * @param updatedDt the updated dt
     */
    public void setUpdatedDt(String updatedDt) {
        this.updatedDt = updatedDt;
    }

    /**
     * Gets created dt.
     *
     * @return the created dt
     */
    public String getCreatedDt() {
        return createdDt;
    }

    /**
     * Sets created dt.
     *
     * @param createdDt the created dt
     */
    public void setCreatedDt(String createdDt) {
        this.createdDt = createdDt;
    }

    /**
     * Gets start date.
     *
     * @return the start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets start date.
     *
     * @param startDate the start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
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
}