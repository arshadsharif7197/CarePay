
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResourcesToScheduleDTO {

    @SerializedName("practice")
    @Expose
    private ResourcesPracticeDTO practice = new ResourcesPracticeDTO();
    @SerializedName("locations")
    @Expose
    private List<AppointmentLocationsDTO> locations = new ArrayList<>();
    @SerializedName("resources")
    @Expose
    private List<AppointmentResourcesDTO> resources = new ArrayList<>();

    /**
     *
     * @return practice
     */
    public ResourcesPracticeDTO getPractice() {
        return practice;
    }

    /**
     *
     * @param practice practice
     */
    public void setPractice(ResourcesPracticeDTO practice) {
        this.practice = practice;
    }

    /**
     *
     * @return locations
     */
    public List<AppointmentLocationsDTO> getLocations() {
        return locations;
    }

    /**
     *
     * @param locations locations
     */
    public void setLocations(List<AppointmentLocationsDTO> locations) {
        this.locations = locations;
    }

    /**
     *
     * @return resources
     */
    public List<AppointmentResourcesDTO> getResources() {
        return resources;
    }

    /**
     *
     * @param resources resources
     */
    public void setResources(List<AppointmentResourcesDTO> resources) {
        this.resources = resources;
    }
}
