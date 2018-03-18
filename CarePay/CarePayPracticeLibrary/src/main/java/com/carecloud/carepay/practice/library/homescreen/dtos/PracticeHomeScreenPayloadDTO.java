package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepaylibray.signinsignup.dto.OptionDTO;
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
    @SerializedName("office_news")
    @Expose
    private List<HomeScreenOfficeNewsDTO> officeNews = null;
    @SerializedName("office_news_post")
    @Expose
    private HomeScreenOfficeNewsDTO officeNewsPost;
    @SerializedName("languages")
    @Expose
    private List<OptionDTO> languages = new ArrayList<>();

    /**
     * The officeNewsPost
     *
     * @return officeNewsPost
     */
    public HomeScreenOfficeNewsDTO getOfficeNewsPost() {
        return officeNewsPost;
    }

    /**
     * The officeNewsPost
     *
     * @param officeNewsPost officeNewsPost
     */
    public void setOfficeNewsPost(HomeScreenOfficeNewsDTO officeNewsPost) {
        this.officeNewsPost = officeNewsPost;
    }

    /**
     * The officeNews
     *
     * @return officeNews
     */
    public List<HomeScreenOfficeNewsDTO> getOfficeNews() {
        return officeNews;
    }

    /**
     * The officeNews
     *
     * @param officeNews officeNews
     */
    public void setOfficeNews(List<HomeScreenOfficeNewsDTO> officeNews) {
        this.officeNews = officeNews;
    }

    /**
     * @return The appointmentCounts
     */
    public HomeScreenAppointmentCountsDTO getAppointmentCounts() {
        return appointmentCounts;
    }

    /**
     * @param appointmentCounts The appointment_counts
     */
    public void setAppointmentCounts(HomeScreenAppointmentCountsDTO appointmentCounts) {
        this.appointmentCounts = appointmentCounts;
    }

    /**
     * @return The alerts
     */
    public HomeScreenAlertsDTO getAlerts() {
        return alerts;
    }

    /**
     * @param alerts The alerts
     */
    public void setAlerts(HomeScreenAlertsDTO alerts) {
        this.alerts = alerts;
    }

    /**
     * @return The userPractices
     */
    public List<UserPracticeDTO> getUserPractices() {
        return userPractices;
    }

    /**
     * @param userPractices The user_practices
     */
    public void setUserPractices(List<UserPracticeDTO> userPractices) {
        this.userPractices = userPractices;
    }

    public List<OptionDTO> getLanguages() {
        return languages;
    }

    public void setLanguages(List<OptionDTO> languages) {
        this.languages = languages;
    }
}
