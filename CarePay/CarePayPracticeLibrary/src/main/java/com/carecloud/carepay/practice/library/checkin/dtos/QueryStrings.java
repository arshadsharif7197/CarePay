package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QueryStrings {

    @SerializedName("practice_mgmt")
    @Expose
    private QueryString practiceManagement = new QueryString();
    @SerializedName("patient_id")
    @Expose
    private QueryString patientId = new QueryString();
    @SerializedName("start_date")
    @Expose
    private QueryString startDate = new QueryString();
    @SerializedName("end_date")
    @Expose
    private QueryString endDate = new QueryString();
    @SerializedName("practice_id")
    @Expose
    private QueryString practiceId = new QueryString();
    @SerializedName("appointment_id")
    @Expose
    private QueryString appointmentId = new QueryString();

    /**
     *
     * @return
     *     The practiceManagement
     */
    public QueryString getPracticeManagement() {
        return practiceManagement;
    }

    /**
     *
     * @param practiceManagement
     *     The practiceManagement
     */
    public void setPracticeManagement(QueryString practiceManagement) {
        this.practiceManagement = practiceManagement;
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
     *     The patientId
     */
    public void setPatientId(QueryString patientId) {
        this.patientId = patientId;
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
     *     The startDate
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
     *     The endDate
     */
    public void setEndDate(QueryString endDate) {
        this.endDate = endDate;
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
     *     The practiceId
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
     *     The appointmentId
     */
    public void setAppointmentId(QueryString appointmentId) {
        this.appointmentId = appointmentId;
    }
}
