
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model for appointment locations.
 */
public class LocationDTO {
    @Expose(serialize = false)
    private boolean error = false;


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("is_visible_appointment_scheduler")
    @Expose
    private Boolean isVisibleAppointmentScheduler;
    @SerializedName("address")
    @Expose
    private AppointmentAddressDTO address = new AppointmentAddressDTO();

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
    public AppointmentAddressDTO getAddress() {
        return address;
    }

    /**
     * 
     * @param address
     *     The address
     */
    public void setAddress(AppointmentAddressDTO address) {
        this.address = address;
    }

    public boolean hasError() {
        return error;
    }

    public void setError(boolean hasError) {
        this.error = hasError;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
