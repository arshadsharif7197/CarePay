
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentResourcesModel {

    @SerializedName("resource")
    @Expose
    private AppointmentResourcesItemModel resource;

    /**
     * 
     * @return
     *     The resource
     */
    public AppointmentResourcesItemModel getResource() {
        return resource;
    }

    /**
     * 
     * @param resource
     *     The resource
     */
    public void setResource(AppointmentResourcesItemModel resource) {
        this.resource = resource;
    }

}
