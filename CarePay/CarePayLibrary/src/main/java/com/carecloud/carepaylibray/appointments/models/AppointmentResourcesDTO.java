
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model for appointment resources.
 */
public class AppointmentResourcesDTO {

    @SerializedName("resource")
    @Expose
    private AppointmentResourcesItemDTO resource = new AppointmentResourcesItemDTO();

    /**
     * 
     * @return
     *     The resource
     */
    public AppointmentResourcesItemDTO getResource() {
        return resource;
    }

    /**
     * 
     * @param resource
     *     The resource
     */
    public void setResource(AppointmentResourcesItemDTO resource) {
        this.resource = resource;
    }

}
