package com.carecloud.carepay.patient.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.carecloud.carepay.patient.appointments.models.AppointmentCalendarEvent;

/**
 * @author pjohnson on 2/27/19.
 */
@Dao
public interface AppointmentCalendarEventDao {

    @Insert
    void insert(AppointmentCalendarEvent event);

    @Delete
    void delete(AppointmentCalendarEvent record);

    @Query("SELECT * FROM AppointmentCalendarEvent WHERE appointmentId == :appointmentId")
    AppointmentCalendarEvent getAppointmentCalendarEvent(String appointmentId);
}
