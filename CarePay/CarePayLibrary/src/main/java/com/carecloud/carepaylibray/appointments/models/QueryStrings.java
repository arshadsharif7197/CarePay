package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Query Strings
 */
public class QueryStrings {

    @SerializedName("start_date")
    @Expose
    private QueryString startDate = new QueryString();
    @SerializedName("end_date")
    @Expose
    private QueryString endDate = new QueryString();
    @SerializedName("practice_mgmt")
    @Expose
    private QueryString practiceMgmt = new QueryString();
    @SerializedName("practice_id")
    @Expose
    private QueryString practiceId = new QueryString();
    @SerializedName("appointment_id")
    @Expose
    private QueryString appointmentId = new QueryString();
    @SerializedName("patient_id")
    @Expose
    private QueryString patientId = new QueryString();

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
     *     The start_date
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
     *     The end_date
     */
    public void setEndDate(QueryString endDate) {
        this.endDate = endDate;
    }

    /**
     *
     * @return
     *     The practiceMgmt
     */
    public QueryString getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     *
     * @param practiceMgmt
     *     The practice_mgmt
     */
    public void setPracticeMgmt(QueryString practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     *
     * @return
     *     The practiceId
     */
    public QueryString getPracticeId() {
        return practiceId;
    }

    /**
     *
     * @param practiceId
     *     The practice_id
     */
    public void setPracticeId(QueryString practiceId) {
        this.practiceId = practiceId;
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
     *     The appointment_id
     */
    public void setAppointmentId(QueryString appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     *
     * @return The patient_id
     */
    public QueryString getPatientId() {
        return patientId;
    }

    /**
     *
     * @param patientId The patientId
     */
    public void setPatientId(QueryString patientId) {
        this.patientId = patientId;
    }
}
