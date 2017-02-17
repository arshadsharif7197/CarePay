package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by arpit_jain1 on 11/7/2016.
 * Model for Links
 */
public class LinksDTO {

    @SerializedName("self")
    @Expose
    private LinkDTO self = new LinkDTO();
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
    @SerializedName("profile_update")
    @Expose
    private TransitionDTO profileUpdate = new TransitionDTO();

    /**
     * @return The self
     */
    public LinkDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(LinkDTO self) {
        this.self = self;
    }

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
     * @return The providersSchedule
     */
    public LinkDTO getProvidersSchedule() {
        return providersSchedule;
    }

    /**
     * @param providersSchedule The providers_schedule
     */
    public void setProvidersSchedule(LinkDTO providersSchedule) {
        this.providersSchedule = providersSchedule;
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

    /**
     * @return The profileUpdate
     */
    public TransitionDTO getProfileUpdate() {
        return profileUpdate;
    }

    /**
     * @param profileUpdate The profileUpdate
     */
    public void setProfileUpdate(TransitionDTO profileUpdate) {
        this.profileUpdate = profileUpdate;
    }

}
