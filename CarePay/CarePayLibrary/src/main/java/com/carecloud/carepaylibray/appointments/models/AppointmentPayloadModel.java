
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for appointment payload.
 */
public class AppointmentPayloadModel implements Serializable {

    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointments = new ArrayList<>();
    @SerializedName("providers")
    @Expose
    private List<AppointmentProvidersDTO> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<AppointmentLocationsDTO> locations = new ArrayList<>();
    @SerializedName("resources")
    @Expose
    private List<AppointmentResourcesDTO> resources = new ArrayList<>();
    @SerializedName("providers_schedule")
    @Expose
    private List<ProvidersScheduleDTO> providersSchedule = new ArrayList<>();
    @SerializedName("cancellation_reasons")
    @Expose
    private List<CancellationReasonDTO> cancellationReasons = new ArrayList<CancellationReasonDTO>();

    /**
     * 
     * @return
     *     The appointments
     */
    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    /**
     * 
     * @param appointments
     *     The appointments
     */
    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    /**
     * 
     * @return
     *     The providers
     */
    public List<AppointmentProvidersDTO> getProviders() {
        return providers;
    }

    /**
     * 
     * @param providers
     *     The providers
     */
    public void setProviders(List<AppointmentProvidersDTO> providers) {
        this.providers = providers;
    }

    /**
     * 
     * @return
     *     The locations
     */
    public List<AppointmentLocationsDTO> getLocations() {
        return locations;
    }

    /**
     * 
     * @param locations
     *     The locations
     */
    public void setLocations(List<AppointmentLocationsDTO> locations) {
        this.locations = locations;
    }

    /**
     * 
     * @return
     *     The resources
     */
    public List<AppointmentResourcesDTO> getResources() {
        return resources;
    }

    /**
     * 
     * @param resources
     *     The resources
     */
    public void setResources(List<AppointmentResourcesDTO> resources) {
        this.resources = resources;
    }

    /**
     *
     * @return
     *     The providersSchedule
     */
    public List<ProvidersScheduleDTO> getProvidersSchedule() {
        return providersSchedule;
    }

    /**
     *
     * @param providersSchedule
     *     The providers_schedule
     */
    public void setProvidersSchedule(List<ProvidersScheduleDTO> providersSchedule) {
        this.providersSchedule = providersSchedule;
    }

    /**
     *
     * @return
     *     The cancellationReasons
     */
    public List<CancellationReasonDTO> getCancellationReasons() {
        return cancellationReasons;
    }

    /**
     *
     * @param cancellationReasons
     *     The cancellation_reasons
     */
    public void setCancellationReasons(List<CancellationReasonDTO> cancellationReasons) {
        this.cancellationReasons = cancellationReasons;
    }

}
