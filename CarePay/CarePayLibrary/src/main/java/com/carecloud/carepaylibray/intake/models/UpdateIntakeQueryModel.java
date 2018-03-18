
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class UpdateIntakeQueryModel {

    @SerializedName("practice_mgmt")
    @Expose
    private PracticeMgmtModel practiceMgmt = new PracticeMgmtModel();
    @SerializedName("practice_id")
    @Expose
    private PracticeIdModel practiceId = new PracticeIdModel();
    @SerializedName("appointment_id")
    @Expose
    private AppointmentModel appointmentId = new AppointmentModel();
    @SerializedName("patient_id")
    @Expose
    private PatientIdModel patientId = new PatientIdModel();
    @SerializedName("findings_id")
    @Expose
    private FindingsIdModel findingsId = new FindingsIdModel();

    /**
     * 
     * @return
     *     The practiceMgmt
     */
    public PracticeMgmtModel getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     * 
     * @param practiceMgmt
     *     The practice_mgmt
     */
    public void setPracticeMgmt(PracticeMgmtModel practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     * 
     * @return
     *     The practiceId
     */
    public PracticeIdModel getPracticeId() {
        return practiceId;
    }

    /**
     * 
     * @param practiceId
     *     The practice_id
     */
    public void setPracticeId(PracticeIdModel practiceId) {
        this.practiceId = practiceId;
    }

    /**
     * 
     * @return
     *     The appointmentId
     */
    public AppointmentModel getAppointmentId() {
        return appointmentId;
    }

    /**
     * 
     * @param appointmentId
     *     The appointment_id
     */
    public void setAppointmentId(AppointmentModel appointmentId) {
        this.appointmentId = appointmentId;
    }

    /**
     * 
     * @return
     *     The patientId
     */
    public PatientIdModel getPatientId() {
        return patientId;
    }

    /**
     * 
     * @param patientId
     *     The patient_id
     */
    public void setPatientId(PatientIdModel patientId) {
        this.patientId = patientId;
    }

    /**
     * 
     * @return
     *     The findingsId
     */
    public FindingsIdModel getFindingsId() {
        return findingsId;
    }

    /**
     * 
     * @param findingsId
     *     The findings_id
     */
    public void setFindingsId(FindingsIdModel findingsId) {
        this.findingsId = findingsId;
    }

}
