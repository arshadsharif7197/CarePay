package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sudhir_pingale on 12/20/2016.
 */
public class AppointmentsSlotsDTO implements Serializable {

    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;

    @Expose(serialize = false)
    private LocationDTO location;
    private transient boolean header;

    /**
     * Gets start time.
     *
     * @return the start time
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets start time.
     *
     * @param startTime the start time
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets end time.
     *
     * @return the end time
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets end time.
     *
     * @param endTime the end time
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @param location appointment Location
     */
    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    /**
     * @return appointment Location
     */
    public LocationDTO getLocation() {
        return location;
    }

    public boolean isHeader() {
        return header;
    }

    public void setHeader(boolean header) {
        this.header = header;
    }
}
