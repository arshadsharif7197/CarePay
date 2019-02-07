
package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ResourcesToScheduleDTO {

    @SerializedName("practice")
    @Expose
    private UserPracticeDTO practice = new UserPracticeDTO();
    @SerializedName("resources")
    @Expose
    private List<AppointmentResourcesItemDTO> resourcesV2 = new ArrayList<>();
    @SerializedName("visit_reasons")
    @Expose
    private List<VisitTypeDTO> visitReasons = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<LocationDTO> locations = new ArrayList<>();

    /**
     * @return practice
     */
    public UserPracticeDTO getPractice() {
        return practice;
    }

    /**
     * @param practice practice
     */
    public void setPractice(UserPracticeDTO practice) {
        this.practice = practice;
    }

    public List<AppointmentResourcesItemDTO> getResourcesV2() {
        return resourcesV2;
    }

    public void setResourcesV2(List<AppointmentResourcesItemDTO> resourcesV2) {
        this.resourcesV2 = resourcesV2;
    }

    public List<VisitTypeDTO> getVisitReasons() {
        return visitReasons;
    }

    public void setVisitReasons(List<VisitTypeDTO> visitReasons) {
        this.visitReasons = visitReasons;
    }

    public List<LocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }
}
