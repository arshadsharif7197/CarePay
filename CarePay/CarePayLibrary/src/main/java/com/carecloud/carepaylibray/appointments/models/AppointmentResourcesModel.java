
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentResourcesModel {

    @SerializedName("resource")
    @Expose
    private AppointmentResourcesItemDto resource;

    /**
     * 
     * @return
     *     The resource
     */
    public AppointmentResourcesItemDto getResource() {
        return resource;
    }

    /**
     * 
     * @param resource
     *     The resource
     */
    public void setResource(AppointmentResourcesItemDto resource) {
        this.resource = resource;
    }

}
