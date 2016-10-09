
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class AppointmentPayloadModel {

    @SerializedName("appointments")
    @Expose
    private List<Appointment> appointments = new ArrayList<>();
    @SerializedName("providers")
    @Expose
    private List<AppointmentProvidersDto> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<AppointmentLocationsDto> locations = new ArrayList<>();
    @SerializedName("resources")
    @Expose
    private List<AppointmentResourcesModel> resources = new ArrayList<>();

    /**
     * 
     * @return
     *     The appointments
     */
    public List<Appointment> getAppointments() {
        return appointments;
    }

    /**
     * 
     * @param appointments
     *     The appointments
     */
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    /**
     * 
     * @return
     *     The providers
     */
    public List<AppointmentProvidersDto> getProviders() {
        return providers;
    }

    /**
     * 
     * @param providers
     *     The providers
     */
    public void setProviders(List<AppointmentProvidersDto> providers) {
        this.providers = providers;
    }

    /**
     * 
     * @return
     *     The locations
     */
    public List<AppointmentLocationsDto> getLocations() {
        return locations;
    }

    /**
     * 
     * @param locations
     *     The locations
     */
    public void setLocations(List<AppointmentLocationsDto> locations) {
        this.locations = locations;
    }

    /**
     * 
     * @return
     *     The resources
     */
    public List<AppointmentResourcesModel> getResources() {
        return resources;
    }

    /**
     * 
     * @param resources
     *     The resources
     */
    public void setResources(List<AppointmentResourcesModel> resources) {
        this.resources = resources;
    }

}
