package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/17/2016.
 * Model for Query Strings.
 */
public class QueryStrings {

    @SerializedName("practice_mgmt")
    @Expose
    private QueryString practiceManagement;
    @SerializedName("practice_id")
    @Expose
    private QueryString practiceId;
    @SerializedName("patient_id")
    @Expose
    private QueryString patientId;
    @SerializedName("appointment_id")
    @Expose
    private QueryString appointmentId;
    @SerializedName("start_date")
    @Expose
    private QueryString startDate;
    @SerializedName("end_date")
    @Expose
    private QueryString endDate;

    /**
     *
     * @return
     *     The practiceManagement
     */
    public QueryString getPracticeMgmt() {
        return practiceManagement;
    }

    /**
     *
     * @param practiceManagement
     *     The practiceManagement
     */
    public void setPracticeMgmt(QueryString practiceManagement) {
        this.practiceManagement = practiceManagement;
    }

    /**
     *
     * @return
     *     The practiceId
     */
    public QueryString getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(QueryString practiceId) {
        this.practiceId = practiceId;
    }

    /**
     *
     * @return
     *     The patientId
     */
    public QueryString getPatientId() {
        return patientId;
    }

    /**
     *
     * @param patientId
     *      The patientId
     */
    public void setPatientId(QueryString patientId) {
        this.patientId = patientId;
    }

    /**
     *
     * @return
     *     The appointmentId
     */
    public QueryString getAppointmentId() {
        return appointmentId;
    }

    /**
     *
     * @param appointmentId
     *      The appointmentId
     */
    public void setAppointmentId(QueryString appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     *
     * @return
     *     The startDate
     */
    public QueryString getStartDate() {
        return startDate;
    }

    /**
     *
     * @param startDate
     *      The startDate
     */
    public void setStartDate(QueryString startDate) {
        this.startDate = startDate;
    }

    /**
     *
     * @return
     *     The endDate
     */
    public QueryString getEndDate() {
        return endDate;
    }

    /**
     *
     * @param endDate
     *      The endDate
     */
    public void setEndDate(QueryString endDate) {
        this.endDate = endDate;
    }
}
