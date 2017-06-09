
package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for appointment payload.
 */
public class AppointmentPayloadModel implements Serializable {

    @SerializedName("languages")
    @Expose
    private List<ResourceLanguageDTO> languages = new ArrayList<>();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointments = new ArrayList<>();
    @SerializedName("practice_patient_ids")
    @Expose
    private List<PracticePatientIdsDTO> practicePatientIds = new ArrayList<>();
    @SerializedName("providers")
    @Expose
    private List<AppointmentProvidersDTO> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<LocationDTO> locations = new ArrayList<>();
    @SerializedName("resources")
    @Expose
    private List<AppointmentResourcesDTO> resources = new ArrayList<>();
    @SerializedName("providers_schedule")
    @Expose
    private List<ProvidersScheduleDTO> providersSchedule = new ArrayList<>();
    @SerializedName("cancellation_reasons")
    @Expose
    private List<CancellationReasonDTO> cancellationReasons = new ArrayList<CancellationReasonDTO>();
    @SerializedName("appointment_availability")
    @Expose
    private AppointmentAvailabilityDataDTO appointmentAvailability = new AppointmentAvailabilityDataDTO();
    @SerializedName("resources_to_schedule")
    @Expose
    private List<ResourcesToScheduleDTO> resourcesToSchedule = new ArrayList<>();
    @SerializedName("appointments_settings")
    @Expose
    private List<AppointmentsSettingDTO> appointmentsSettings = new ArrayList<AppointmentsSettingDTO>();
    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographicDTO = new DemographicsSettingsDemographicsDTO();
    @SerializedName("patient_balances")
    @Expose
    private List<PatientBalanceDTO> patientBalances = new ArrayList<>();

    /**
     * @return languages
     */
    public List<ResourceLanguageDTO> getLanguages() {
        return languages;
    }

    /**
     * @param languages languages
     */
    public void setLanguages(List<ResourceLanguageDTO> languages) {
        this.languages = languages;
    }

    /**
     * @return The appointments
     */
    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    /**
     * @param appointments The appointments
     */
    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    /**
     * @return The providers
     */
    public List<AppointmentProvidersDTO> getProviders() {
        return providers;
    }

    /**
     * @param providers The providers
     */
    public void setProviders(List<AppointmentProvidersDTO> providers) {
        this.providers = providers;
    }

    /**
     * @return The locations
     */
    public List<LocationDTO> getLocations() {
        return locations;
    }

    /**
     * @param locations The locations
     */
    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }

    /**
     * @return The resources
     */
    public List<AppointmentResourcesDTO> getResources() {
        return resources;
    }

    /**
     * @param resources The resources
     */
    public void setResources(List<AppointmentResourcesDTO> resources) {
        this.resources = resources;
    }

    /**
     * @return The providersSchedule
     */
    public List<ProvidersScheduleDTO> getProvidersSchedule() {
        return providersSchedule;
    }

    /**
     * @param providersSchedule The providers_schedule
     */
    public void setProvidersSchedule(List<ProvidersScheduleDTO> providersSchedule) {
        this.providersSchedule = providersSchedule;
    }

    /**
     * @return The cancellationReasons
     */
    public List<CancellationReasonDTO> getCancellationReasons() {
        return cancellationReasons;
    }

    /**
     * @param cancellationReasons The cancellation_reasons
     */
    public void setCancellationReasons(List<CancellationReasonDTO> cancellationReasons) {
        this.cancellationReasons = cancellationReasons;
    }

    /**
     * Gets appointmentAvailability.
     *
     * @return the appointmentAvailability
     */
    public AppointmentAvailabilityDataDTO getAppointmentAvailability() {
        return appointmentAvailability;
    }

    /**
     * Sets appointmentAvailability.
     *
     * @param appointmentAvailability the appointmentAvailability
     */
    public void setAppointmentAvailability(AppointmentAvailabilityDataDTO appointmentAvailability) {
        this.appointmentAvailability = appointmentAvailability;
    }

    /**
     * @return resourcesToSchedule
     */
    public List<ResourcesToScheduleDTO> getResourcesToSchedule() {
        return resourcesToSchedule;
    }

    /**
     * @param resourcesToSchedule resourcesToSchedule
     */
    public void setResourcesToSchedule(List<ResourcesToScheduleDTO> resourcesToSchedule) {
        this.resourcesToSchedule = resourcesToSchedule;
    }

    /**
     * @return practice_patent_ids
     */
    public List<PracticePatientIdsDTO> getPracticePatientIds() {
        return practicePatientIds;
    }

    /**
     * @param practicePatientIds practicePatientIds
     */
    public void setPracticePatientIds(List<PracticePatientIdsDTO> practicePatientIds) {
        this.practicePatientIds = practicePatientIds;
    }

    /**
     * @return appointmentsSettings
     */
    public List<AppointmentsSettingDTO> getAppointmentsSettings() {
        return appointmentsSettings;
    }

    /**
     * @param appointmentsSettings appointmentsSettings
     */
    public void setAppointmentsSettings(List<AppointmentsSettingDTO> appointmentsSettings) {
        this.appointmentsSettings = appointmentsSettings;
    }

    public DemographicsSettingsDemographicsDTO getDemographicDTO() {
        return demographicDTO;
    }

    public void setDemographicDTO(DemographicsSettingsDemographicsDTO demographicDTO) {
        this.demographicDTO = demographicDTO;
    }

    public List<PatientBalanceDTO> getPatientBalances() {
        return patientBalances;
    }

    public void setPatientBalances(List<PatientBalanceDTO> patientBalances) {
        this.patientBalances = patientBalances;
    }
}
