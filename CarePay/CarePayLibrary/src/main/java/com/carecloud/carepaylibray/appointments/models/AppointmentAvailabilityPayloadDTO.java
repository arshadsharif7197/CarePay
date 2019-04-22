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
    private LocationDTO location = new LocationDTO();
    @SerializedName("visit_reason")
    @Expose
    private VisitTypeDTO visitReason = new VisitTypeDTO();
    @SerializedName("resource")
    @Expose
    private AppointmentResourcesItemDTO resource = new AppointmentResourcesItemDTO();
    @SerializedName("slots")
    @Expose
    private List<AppointmentsSlotsDTO> slots = new ArrayList<>();

    /**
     * Gets location.
     *
     * @return the location
     */
    public LocationDTO getLocation() {
        return location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    /**
     * Gets visit reason.
     *
     * @return the visit reason
     */
    public VisitTypeDTO getVisitReason() {
        return visitReason;
    }

    /**
     * Sets visit reason.
     *
     * @param visitReason the visit reason
     */
    public void setVisitReason(VisitTypeDTO visitReason) {
        this.visitReason = visitReason;
    }

    /**
     * Gets resource.
     *
     * @return the resource
     */
    public AppointmentResourcesItemDTO getResource() {
        return resource;
    }

    /**
     * Sets resource.
     *
     * @param resource the resource
     */
    public void setResource(AppointmentResourcesItemDTO resource) {
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
