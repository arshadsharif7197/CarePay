package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class PracticeHomeScreenPayloadDTO {
    @SerializedName("appointment_counts")
    @Expose
    private HomeScreenAppointmentCountsDTO appointmentCounts = new HomeScreenAppointmentCountsDTO();
    @SerializedName("alerts")
    @Expose
    private HomeScreenAlertsDTO alerts = new HomeScreenAlertsDTO();
    @SerializedName("user_practices")
    @Expose
    private List<UserPracticeDTO> userPractices = new ArrayList<>();


    /**
     *
     * @return
     * The appointmentCounts
     */
    public HomeScreenAppointmentCountsDTO getAppointmentCounts() {
        return appointmentCounts;
    }

    /**
     *
     * @param appointmentCounts
     * The appointment_counts
     */
    public void setAppointmentCounts(HomeScreenAppointmentCountsDTO appointmentCounts) {
        this.appointmentCounts = appointmentCounts;
    }

    /**
     *
     * @return
     * The alerts
     */
    public HomeScreenAlertsDTO getAlerts() {
        return alerts;
    }

    /**
     *
     * @param alerts
     * The alerts
     */
    public void setAlerts(HomeScreenAlertsDTO alerts) {
        this.alerts = alerts;
    }

    /**
     *
     * @return
     * The userPractices
     */
    public List<UserPracticeDTO> getUserPractices() {
        return userPractices;
    }

    /**
     *
     * @param userPractices
     * The user_practices
     */
    public void setUserPractices(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
    }
}
