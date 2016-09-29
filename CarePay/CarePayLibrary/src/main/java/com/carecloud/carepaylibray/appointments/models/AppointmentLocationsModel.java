
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentLocationsModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_visible_appointment_scheduler")
    @Expose
    private Boolean isVisibleAppointmentScheduler;
    @SerializedName("address")
    @Expose
    private AppointmentLocationsAddressModel address;

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The isVisibleAppointmentScheduler
     */
    public Boolean getIsVisibleAppointmentScheduler() {
        return isVisibleAppointmentScheduler;
    }

    /**
     * 
     * @param isVisibleAppointmentScheduler
     *     The is_visible_appointment_scheduler
     */
    public void setIsVisibleAppointmentScheduler(Boolean isVisibleAppointmentScheduler) {
        this.isVisibleAppointmentScheduler = isVisibleAppointmentScheduler;
    }

    /**
     * 
     * @return
     *     The address
     */
    public AppointmentLocationsAddressModel getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(AppointmentLocationsAddressModel address) {
        this.address = address;
    }

}
