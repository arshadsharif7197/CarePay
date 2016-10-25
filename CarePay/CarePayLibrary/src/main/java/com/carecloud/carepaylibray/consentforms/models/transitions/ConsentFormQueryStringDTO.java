package com.carecloud.carepaylibray.consentforms.models.transitions;

/**
 * Created by Rahul on 10/21/16.
 */

        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ConsentFormQueryStringDTO {

    @SerializedName("practice_mgmt")
    @Expose
    private ConsentFormsPracticeMgmtDTO practiceMgmt;
    @SerializedName("practice_id")
    @Expose
    private ConsentFormsPracticeIDDTO practiceId;
    @SerializedName("appointment_id")
    @Expose
    private ConsentFormsAppointmentIDDTO appointmentId;

    /**
     *
     * @return
     * The practiceMgmt
     */
    public ConsentFormsPracticeMgmtDTO getPracticeMgmt() {
        return practiceMgmt;
    }

    /**
     *
     * @param practiceMgmt
     * The practice_mgmt
     */
    public void setPracticeMgmt(ConsentFormsPracticeMgmtDTO practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    /**
     *
     * @return
     * The practiceId
     */
    public ConsentFormsPracticeIDDTO getPracticeId() {
        return practiceId;
    }

    /**
     *
     * @param practiceId
     * The practice_id
     */
    public void setPracticeId(ConsentFormsPracticeIDDTO practiceId) {
        this.practiceId = practiceId;
    }

    /**
     *
     * @return
     * The appointmentId
     */
    public ConsentFormsAppointmentIDDTO getAppointmentId() {
        return appointmentId;
    }

    /**
     *
     * @param appointmentId
     * The appointment_id
     */
    public void setAppointmentId(ConsentFormsAppointmentIDDTO appointmentId) {
        this.appointmentId = appointmentId;
    }

}
