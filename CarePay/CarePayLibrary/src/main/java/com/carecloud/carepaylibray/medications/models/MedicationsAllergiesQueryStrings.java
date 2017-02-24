package com.carecloud.carepaylibray.medications.models;

import com.carecloud.carepaylibray.appointments.models.QueryString;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/15/17.
 */

public class MedicationsAllergiesQueryStrings {

    @SerializedName("practice_mgmt")
    @Expose
    private QueryString practiceMgmt =  new QueryString();

    @SerializedName("practice_id")
    @Expose
    private QueryString practiceId = new QueryString();

    @SerializedName("patient_id")
    @Expose
    private QueryString patientId = new QueryString();

    @SerializedName("appointment_id")
    @Expose
    private QueryString appointmentId = new QueryString();

    @SerializedName("search")
    @Expose
    private QueryString search = new QueryString();

    public QueryString getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(QueryString practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public QueryString getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(QueryString practiceId) {
        this.practiceId = practiceId;
    }

    public QueryString getPatientId() {
        return patientId;
    }

    public void setPatientId(QueryString patientId) {
        this.patientId = patientId;
    }

    public QueryString getSearch() {
        return search;
    }

    public void setSearch(QueryString search) {
        this.search = search;
    }

    public QueryString getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(QueryString appointmentId) {
        this.appointmentId = appointmentId;
    }
}
