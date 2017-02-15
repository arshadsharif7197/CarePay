package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sudhir_pingale on 12/20/2016.
 */

public class AppointmentAvailabilityPayloadDTO implements Serializable {

    @SerializedName("location")
    @Expose
    private AppointmentLocationsDTO location;
    @SerializedName("visit_reason")
    @Expose
    private ProvidersReasonDTO visitReason;
    @SerializedName("resource")
    @Expose
    private AppointmentResourceDTO resource;
    @SerializedName("slots")
    @Expose
    private List<AppointmentsSlotsDTO> slots = new ArrayList<>();

    /**
     * Gets location.
     *
     * @return the location
     */
    public AppointmentLocationsDTO getLocation() {
        return location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(AppointmentLocationsDTO location) {
        this.location = location;
    }

    /**
     * Gets visit reason.
     *
     * @return the visit reason
     */
    public ProvidersReasonDTO getVisitReason() {
        return visitReason;
    }

    /**
     * Sets visit reason.
     *
     * @param visitReason the visit reason
     */
    public void setVisitReason(ProvidersReasonDTO visitReason) {
        this.visitReason = visitReason;
    }

    /**
     * Gets resource.
     *
     * @return the resource
     */
    public AppointmentResourceDTO getResource() {
        return resource;
    }

    /**
     * Sets resource.
     *
     * @param resource the resource
     */
    public void setResource(AppointmentResourceDTO resource) {
        this.resource = resource;
    }

    /**
     * Gets slots.
     *
     * @return the slots
     */
    public List<AppointmentsSlotsDTO> getSlots() {
        return slots;
    }

    /**
     * Sets slots.
     *
     * @param slots the slots
     */
    public void setSlots(List<AppointmentsSlotsDTO> slots) {
        this.slots = slots;
    }
}
