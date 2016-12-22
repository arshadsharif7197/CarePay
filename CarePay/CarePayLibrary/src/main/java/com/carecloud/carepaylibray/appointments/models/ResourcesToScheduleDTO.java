
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResourcesToScheduleDTO {

    @SerializedName("practice")
    @Expose
    private ResourcesPracticeDTO practice;
    @SerializedName("locations")
    @Expose
    private List<ResourceLanguageDTO> locations = null;
    @SerializedName("resources")
    @Expose
    private List<AppointmentResourcesDTO> resources = null;

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
    public List<ResourceLanguageDTO> getLocations() {
        return locations;
    }

    /**
     *
     * @param locations locations
     */
    public void setLocations(List<ResourceLanguageDTO> locations) {
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
