package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LabelModel {

    @SerializedName("workflow_stage_title")
    @Expose
    private String workflowStageTitle;
    @SerializedName("demographics_checkin_heading")
    @Expose
    private String demographicsCheckinHeading;
    @SerializedName("demographics_appointments_heading")
    @Expose
    private String demographicsAppointmentsHeading;
    @SerializedName("intake_form_heading")
    @Expose
    private String intakeFormHeading;
    @SerializedName("next_question_button_text")
    @Expose
    private String nextQuestionButtonText;
    @SerializedName("finish_questions_button_text")
    @Expose
    private String finishQuestionsButtonText;
    @SerializedName("intake_form_fill_warning")
    @Expose
    private String intakeFormFillWarningTitle;


    /**
     *
     * @return
     * The workflowStageTitle
     */
    public String getWorkflowStageTitle() {
        return workflowStageTitle;
    }

    /**
     *
     * @param workflowStageTitle
     * The workflow_stage_title
     */
    public void setWorkflowStageTitle(String workflowStageTitle) {
        this.workflowStageTitle = workflowStageTitle;
    }

    /**
     *
     * @return
     * The demographicsCheckinHeading
     */
    public String getDemographicsCheckinHeading() {
        return demographicsCheckinHeading;
    }

    /**
     *
     * @param demographicsCheckinHeading
     * The demographics_checkin_heading
     */
    public void setDemographicsCheckinHeading(String demographicsCheckinHeading) {
        this.demographicsCheckinHeading = demographicsCheckinHeading;
    }

    /**
     *
     * @return
     * The demographicsAppointmentsHeading
     */
    public String getDemographicsAppointmentsHeading() {
        return demographicsAppointmentsHeading;
    }

    /**
     *
     * @param demographicsAppointmentsHeading
     * The demographics_appointments_heading
     */
    public void setDemographicsAppointmentsHeading(String demographicsAppointmentsHeading) {
        this.demographicsAppointmentsHeading = demographicsAppointmentsHeading;
    }

    /**
     *
     * @return
     * The intakeFormHeading
     */
    public String getIntakeFormHeading() {
        return intakeFormHeading;
    }

    /**
     *
     * @param intakeFormHeading
     * The intake_form_heading
     */
    public void setIntakeFormHeading(String intakeFormHeading) {
        this.intakeFormHeading = intakeFormHeading;
    }

    /**
     *
     * @return
     * The nextQuestionButtonText
     */
    public String getNextQuestionButtonText() {
        return nextQuestionButtonText;
    }

    /**
     *
     * @param nextQuestionButtonText
     * The next_question_button_text
     */
    public void setNextQuestionButtonText(String nextQuestionButtonText) {
        this.nextQuestionButtonText = nextQuestionButtonText;
    }

    /**
     *
     * @return
     * The finishQuestionsButtonText
     */
    public String getFinishQuestionsButtonText() {
        return finishQuestionsButtonText;
    }

    /**
     *
     * @param finishQuestionsButtonText
     * The finish_questions_button_text
     */
    public void setFinishQuestionsButtonText(String finishQuestionsButtonText) {
        this.finishQuestionsButtonText = finishQuestionsButtonText;
    }

    /**
     *
     * @return
     * The intakeFormFillWarningTitle
     */
    public String getIntakeFormFillWarningTitle() {
        return intakeFormFillWarningTitle;
    }

    /**
     *
     * @param intakeFormFillWarningTitle
     * The intake_form_fill_warning
     */
    public void setIntakeFormFillWarningTitle(String intakeFormFillWarningTitle) {
        this.intakeFormFillWarningTitle = intakeFormFillWarningTitle;
    }


}
