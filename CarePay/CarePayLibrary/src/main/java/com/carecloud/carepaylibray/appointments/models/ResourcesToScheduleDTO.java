
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
    private List<LocationDTO> locations = new ArrayList<>();
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
    public List<LocationDTO> getLocations() {
        return locations;
    }

    /**
     *
     * @param locations locations
     */
    public void setLocations(List<LocationDTO> locations) {
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
