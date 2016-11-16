package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepaylibray.appointments.models.QueryString;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by sudhir_pingale on 11/11/2016.
 * Model for Query Strings
 */
public class QueryStrings {

    @SerializedName("start_date")
    @Expose
    private QueryString startDate;
    @SerializedName("end_date")
    @Expose
    private QueryString endDate;
    @SerializedName("practice_mgmt")
    @Expose
    private QueryString practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private QueryString practiceId;
    @SerializedName("appointment_id")
    @Expose
    private QueryString appointmentId;

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


}
