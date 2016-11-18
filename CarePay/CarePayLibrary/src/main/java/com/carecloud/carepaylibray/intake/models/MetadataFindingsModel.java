
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class MetadataFindingsModel {

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
    @SerializedName("findings_id")
    @Expose
    private String findingsId;

    /**
     * 
     * @return
     *     The practiceMgmt
     */
    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     * 
     * @param practiceMgmt
     *     The practice_mgmt
     */
    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     * 
     * @return
     *     The practiceId
     */
    public String getPracticeId() {
        return practiceId;
    }

    /**
     * 
     * @param practiceId
     *     The practice_id
     */
    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
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
     *     The appointmentId
     */
    public String getAppointmentId() {
        return appointmentId;
    }

    /**
     * 
     * @param appointmentId
     *     The appointment_id
     */
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * 
     * @return
     *     The findingsId
     */
    public String getFindingsId() {
        return findingsId;
    }

    /**
     * 
     * @param findingsId
     *     The findings_id
     */
    public void setFindingsId(String findingsId) {
        this.findingsId = findingsId;
    }

}
