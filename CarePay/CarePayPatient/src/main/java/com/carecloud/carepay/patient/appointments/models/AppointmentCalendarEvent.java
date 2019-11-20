package com.carecloud.carepay.patient.appointments.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

/**
 * @author pjohnson on 4/16/19.
 */
@Entity(tableName = "AppointmentCalendarEvent")
public class AppointmentCalendarEvent {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private long eventId;
    @NonNull
    private String appointmentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
}
