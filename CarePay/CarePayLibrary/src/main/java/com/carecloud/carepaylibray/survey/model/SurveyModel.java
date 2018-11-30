package com.carecloud.carepaylibray.survey.model;

import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author pjohnson on 6/09/18.
 */
public class SurveyModel {

    @Expose
    @SerializedName("title")
    private String title;
    @Expose
    @SerializedName("type")
    private String type;
    @Expose
    @SerializedName("uuid")
    private String uuid;
    @Expose
    @SerializedName("version")
    private String version;
    @Expose
    @SerializedName("appointment_id")
    private String appointmentId;
    @Expose
    @SerializedName("feedback")
    private String feedback;
    @Expose
    @SerializedName("survey_rate")
    private double surveyRating;
    @Expose
    @SerializedName("questions")
    private List<SurveyQuestionDTO> questions;
    @Expose
    @SerializedName("responses")
    private List<SurveyQuestionDTO> responses;
    @Expose
    @SerializedName("metadata")
    private SurveyModelMetadata metadata;
    @Expose
    @SerializedName("appointment")
    private AppointmentDTO appointment;

    private transient boolean alreadyFilled;
    private transient boolean zeroAnswers;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<SurveyQuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionDTO> questions) {
        this.questions = questions;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public SurveyModelMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(SurveyModelMetadata metadata) {
        this.metadata = metadata;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public double getSurveyRating() {
        return surveyRating;
    }

    public void setSurveyRating(double surveyRating) {
        this.surveyRating = surveyRating;
    }

    public List<SurveyQuestionDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<SurveyQuestionDTO> responses) {
        this.responses = responses;
    }

    public boolean isAlreadyFilled() {
        return alreadyFilled;
    }

    public void setAlreadyFilled() {
        alreadyFilled = true;
    }

    public void setZeroAnswers(boolean zeroAnswers) {
        this.zeroAnswers = zeroAnswers;
    }

    public boolean isZeroAnswers() {
        return zeroAnswers;
    }

    public AppointmentDTO getAppointment() {
        return appointment;
    }

    public void setAppointment(AppointmentDTO appointment) {
        this.appointment = appointment;
    }
}
