package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.base.dtos.BaseLinks;
import com.carecloud.carepaylibray.base.dtos.LinkDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Links
 */
public class LinksDTO extends BaseLinks {

    @SerializedName("demographics")
    @Expose
    private LinkDTO demographics = new LinkDTO();
    @SerializedName("appointments")
    @Expose
    private TransitionDTO appointments = new TransitionDTO();
    @SerializedName("patient_balances")
    @Expose
    private TransitionDTO patientBalances = new TransitionDTO();
    @SerializedName("resources_to_schedule")
    @Expose
    private TransitionDTO resourcesToSchedule = new TransitionDTO();
    @SerializedName("providers_schedule")
    @Expose
    private LinkDTO providersSchedule = new LinkDTO();
    @SerializedName("appointment_availability")
    @Expose
    private TransitionDTO appointmentAvailability = new TransitionDTO();
    @SerializedName("find_patient")
    @Expose
    private TransitionDTO findPatient = new TransitionDTO();
    @SerializedName("checkin_status")
    @Expose
    private TransitionDTO checkinStatus = new TransitionDTO();
    @SerializedName("queue_status")
    @Expose
    private TransitionDTO queueStatus = new TransitionDTO();
    @SerializedName("page")
    @Expose
    private TransitionDTO pagePatient = new TransitionDTO();
    @SerializedName("notifications")
    @Expose
    private TransitionDTO notifications = new TransitionDTO();
    @SerializedName("pinpad")
    @Expose
    private TransitionDTO pinpad = new TransitionDTO();
    @SerializedName("all_practice_forms")
    @Expose
    private TransitionDTO allPracticeForms = new TransitionDTO();
    @SerializedName("patient_payments")
    @Expose
    private TransitionDTO patientPayments;
    @SerializedName("practice_appointments")
    @Expose
    private TransitionDTO practiceAppointments = new TransitionDTO();
    @SerializedName("appointment_status")
    @Expose
    private TransitionDTO appointmentStatus = new TransitionDTO();
    @SerializedName(value = "language_metadata", alternate = "language")
    @Expose
    private TransitionDTO language = new TransitionDTO();
    @SerializedName("shop")
    @Expose
    private TransitionDTO shop = new TransitionDTO();
    @Expose
    @SerializedName("visit_summary")
    private TransitionDTO visitSummary = new TransitionDTO();
    @Expose
    @SerializedName("visit_summary_status")
    private TransitionDTO visitSummaryStatus = new TransitionDTO();
    @SerializedName("video_visit")
    @Expose
    private TransitionDTO videoVisit = new TransitionDTO();

    /**
     * @return The demographics
     */
    public LinkDTO getDemographics() {
        return demographics;
    }

    /**
     * @param demographics The demographics
     */
    public void setDemographics(LinkDTO demographics) {
        this.demographics = demographics;
    }

    /**
     * @return The appointments
     */
    public TransitionDTO getAppointments() {
        return appointments;
    }

    /**
     * @param appointments The appointments
     */
    public void setAppointments(TransitionDTO appointments) {
        this.appointments = appointments;
    }

    /**
     * @return The patientBalances
     */
    public TransitionDTO getPatientBalances() {
        return patientBalances;
    }

    /**
     * @param patientBalances The patient_balances
     */
    public void setPatientBalances(TransitionDTO patientBalances) {
        this.patientBalances = patientBalances;
    }

    /**
     * Gets appointment availability.
     *
     * @return the appointment availability
     */
    public TransitionDTO getAppointmentAvailability() {
        return appointmentAvailability;
    }

    /**
     * Sets appointment availability.
     *
     * @param appointmentAvailability the appointment availability
     */
    public void setAppointmentAvailability(TransitionDTO appointmentAvailability) {
        this.appointmentAvailability = appointmentAvailability;
    }

    /*
     *
     * @return resourcesToSchedule
     */
    public TransitionDTO getResourcesToSchedule() {
        return resourcesToSchedule;
    }

    /**
     * @param resourcesToSchedule resourcesToSchedule
     */
    public void setResourcesToSchedule(TransitionDTO resourcesToSchedule) {
        this.resourcesToSchedule = resourcesToSchedule;
    }

    public TransitionDTO getFindPatient() {
        return findPatient;
    }

    public TransitionDTO getCheckinStatus() {
        return checkinStatus;
    }

    public TransitionDTO getQueueStatus() {
        return queueStatus;
    }

    public TransitionDTO getPagePatient() {
        return pagePatient;
    }

    public void setPagePatient(TransitionDTO pagePatient) {
        this.pagePatient = pagePatient;
    }

    public TransitionDTO getNotifications() {
        return notifications;
    }

    public void setNotifications(TransitionDTO notifications) {
        this.notifications = notifications;
    }

    public TransitionDTO getPinpad() {
        return pinpad;
    }

    public void setPinpad(TransitionDTO pinpad) {
        this.pinpad = pinpad;
    }

    public TransitionDTO getAllPracticeForms() {
        return allPracticeForms;
    }

    public void setAllPracticeForms(TransitionDTO allPracticeForms) {
        this.allPracticeForms = allPracticeForms;
    }

    public TransitionDTO getPatientPayments() {
        return patientPayments;
    }

    public void setPatientPayments(TransitionDTO patientPayments) {
        this.patientPayments = patientPayments;
    }

    public TransitionDTO getPracticeAppointments() {
        return practiceAppointments;
    }

    public void setPracticeAppointments(TransitionDTO practiceAppointments) {
        this.practiceAppointments = practiceAppointments;
    }

    public TransitionDTO getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(TransitionDTO appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public TransitionDTO getLanguage() {
        return language;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }

    public TransitionDTO getShop() {
        return shop;
    }

    public void setShop(TransitionDTO shop) {
        this.shop = shop;
    }

    public TransitionDTO getVisitSummary() {
        return visitSummary;
    }

    public void setVisitSummary(TransitionDTO visitSummary) {
        this.visitSummary = visitSummary;
    }

    public TransitionDTO getVisitSummaryStatus() {
        return visitSummaryStatus;
    }

    public void setVisitSummaryStatus(TransitionDTO visitSummaryStatus) {
        this.visitSummaryStatus = visitSummaryStatus;
    }

    public TransitionDTO getVideoVisit() {
        return videoVisit;
    }

    public void setVideoVisit(TransitionDTO videoVisit) {
        this.videoVisit = videoVisit;
    }
}
