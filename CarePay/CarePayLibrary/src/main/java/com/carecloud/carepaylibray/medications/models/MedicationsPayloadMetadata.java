package com.carecloud.carepaylibray.medications.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 2/16/17.
 */

public class MedicationsPayloadMetadata {

    @SerializedName("practice_mgmt")
    @Expose
    private String practiceMgmt;

    @SerializedName("practice_id")
    @Expose
    private String practiceId;

    @SerializedName("patient_id")
    @Expose
    private String patientId;

    @SerializedName("appointment_id")
    @Expose
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
