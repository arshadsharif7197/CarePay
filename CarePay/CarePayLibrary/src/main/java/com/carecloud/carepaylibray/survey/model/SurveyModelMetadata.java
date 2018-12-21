package com.carecloud.carepaylibray.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 17/09/18.
 */
public class SurveyModelMetadata {

    @Expose
    @SerializedName("practice_mgmt")
    private String practiceMgmt;
    @Expose
    @SerializedName("practice_id")
    private String practiceId;
    @Expose
    @SerializedName("created_dt")
    private String createdDate;
    @Expose
    @SerializedName("visit_dt")
    private String visitDate;
    @Expose
    @SerializedName("appointment_id")
    private String appointmentId;

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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
