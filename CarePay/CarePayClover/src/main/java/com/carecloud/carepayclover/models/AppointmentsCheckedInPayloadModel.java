package com.carecloud.carepayclover.models;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 */
import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class AppointmentsCheckedInPayloadModel {
    @SerializedName("appointments")
    @Expose
    private List<AppointmentsCheckedInPayloadAppointmentModel> appointments = new ArrayList<AppointmentsCheckedInPayloadAppointmentModel>();

    /**
     *
     * @return
     * The appointments
     */
    public List<AppointmentsCheckedInPayloadAppointmentModel> getAppointments() {
        return appointments;
    }

    /**
     *
     * @param appointments
     * The appointments
     */
    public void setAppointments(List<AppointmentsCheckedInPayloadAppointmentModel> appointments) {
        this.appointments = appointments;
    }
}
