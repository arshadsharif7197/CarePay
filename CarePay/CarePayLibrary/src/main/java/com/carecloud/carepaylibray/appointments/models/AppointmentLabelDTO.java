
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AppointmentLabelDTO implements Serializable {

    @SerializedName("appointments_heading")
    @Expose
    private String appointmentsHeading;
    @SerializedName("cancel_appointments_heading")
    @Expose
    private String cancelAppointmentsHeading;
    @SerializedName("choose_provider_heading")
    @Expose
    private String chooseProviderHeading;
    @SerializedName("other_provider_heading")
    @Expose
    private String otherProviderHeading;
    @SerializedName("visit_type_heading")
    @Expose
    private String visitTypeHeading;
    @SerializedName("pick_date_heading")
    @Expose
    private String pickDateHeading;
    @SerializedName("available_hours_heading")
    @Expose
    private String availableHoursHeading;
    @SerializedName("appointments_check_in_early_prompt")
    @Expose
    private String appointmentsCheckInEarlyPrompt;
    @SerializedName("dismiss_message")
    @Expose
    private String dismissMessage;
    @SerializedName("other_appointment_heading_menu")
    @Expose
    private String otherAppointmentHeadingMenu;
    @SerializedName("appointments_checked_in_label")
    @Expose
    private String appointmentsCheckedInLabel;
    @SerializedName("appointments_check_in_early")
    @Expose
    private String appointmentsCheckInEarly;
    @SerializedName("today_appointments_heading")
    @Expose
    private String todayAppointmentsHeading;
    @SerializedName("upcoming_appointments_heading")
    @Expose
    private String upcomingAppointmentsHeading;
    @SerializedName("missed_appointments_heading")
    @Expose
    private String missedAppointmentsHeading;

    /**
     * @return The appointmentsHeading
     */
    public String getAppointmentsHeading() {
        return appointmentsHeading;
    }

    /**
     * @param appointmentsHeading The appointments_heading
     */
    public void setAppointmentsHeading(String appointmentsHeading) {
        this.appointmentsHeading = appointmentsHeading;
    }

    /**
     * @return The cancelAppointmentsHeading
     */
    public String getCancelAppointmentsHeading() {
        return cancelAppointmentsHeading;
    }

    /**
     * @param cancelAppointmentsHeading The cancel_appointments_heading
     */
    public void setCancelAppointmentsHeading(String cancelAppointmentsHeading) {
        this.cancelAppointmentsHeading = cancelAppointmentsHeading;
    }

    /**
     * @return The chooseProviderHeading
     */
    public String getChooseProviderHeading() {
        return chooseProviderHeading;
    }

    /**
     * @param chooseProviderHeading The choose_provider_heading
     */
    public void setChooseProviderHeading(String chooseProviderHeading) {
        this.chooseProviderHeading = chooseProviderHeading;
    }

    /**
     * @return The visitTypeHeading
     */
    public String getVisitTypeHeading() {
        return visitTypeHeading;
    }

    /**
     * @param visitTypeHeading The visit_type_heading
     */
    public void setVisitTypeHeading(String visitTypeHeading) {
        this.visitTypeHeading = visitTypeHeading;
    }

    /**
     * @return The pickDateHeading
     */
    public String getPickDateHeading() {
        return pickDateHeading;
    }

    /**
     * @param pickDateHeading The pick_date_heading
     */
    public void setPickDateHeading(String pickDateHeading) {
        this.pickDateHeading = pickDateHeading;
    }

    /**
     * @return The availableHoursHeading
     */
    public String getAvailableHoursHeading() {
        return availableHoursHeading;
    }

    /**
     * @param availableHoursHeading The available_hours_heading
     */
    public void setAvailableHoursHeading(String availableHoursHeading) {
        this.availableHoursHeading = availableHoursHeading;
    }

    /**
     * @return The appointmentsCheckInEarlyPrompt
     */
    public String getAppointmentsCheckInEarlyPrompt() {
        return appointmentsCheckInEarlyPrompt;
    }

    /**
     * @param appointmentsCheckInEarlyPrompt The appointments_check_in_early_prompt
     */
    public void setAppointmentsCheckInEarlyPrompt(String appointmentsCheckInEarlyPrompt) {
        this.appointmentsCheckInEarlyPrompt = appointmentsCheckInEarlyPrompt;
    }

    /**
     * @return The dismissMessage
     */
    public String getDismissMessage() {
        return dismissMessage;
    }

    /**
     * @param dismissMessage The dismiss_message
     */
    public void setDismissMessage(String dismissMessage) {
        this.dismissMessage = dismissMessage;
    }

    /**
     * @return The appointmentsCheckInEarly
     */
    public String getAppointmentsCheckInEarly() {
        return appointmentsCheckInEarly;
    }

    /**
     * @param appointmentsCheckInEarly The appointments_check_in_early
     */
    public void setAppointmentsCheckInEarly(String appointmentsCheckInEarly) {
        this.appointmentsCheckInEarly = appointmentsCheckInEarly;
    }

    /**
     * @return The todayAppointmentsHeading;
     */
    public String getTodayAppointmentsHeading() {
        return todayAppointmentsHeading;
    }

    /**
     * @param todayAppointmentsHeading The today_appointments_heading
     */
    public void setTodayAppointmentsHeading(String todayAppointmentsHeading) {
        this.todayAppointmentsHeading = todayAppointmentsHeading;
    }

    /**
     * @return The upcomingAppointmentsHeading
     */
    public String getUpcomingAppointmentsHeading() {
        return upcomingAppointmentsHeading;
    }

    /**
     * @param upcomingAppointmentsHeading The upcoming_appointments_heading
     */
    public void setUpcomingAppointmentsHeading(String upcomingAppointmentsHeading) {
        this.upcomingAppointmentsHeading = upcomingAppointmentsHeading;
    }

    /**
     * @return The missedAppointmentsHeading
     */
    public String getMissedAppointmentsHeading() {
        return missedAppointmentsHeading;
    }

    /**
     * @param missedAppointmentsHeading The missed_appointments_heading
     */
    public void setMissedAppointmentsHeading(String missedAppointmentsHeading) {
        this.missedAppointmentsHeading = missedAppointmentsHeading;
    }

    /**
     * @return The otherProviderHeading
     */
    public String getOtherProviderHeading() {
        return otherProviderHeading;
    }

    /**
     * @param otherProviderHeading The other_provider_heading
     */
    public void setOtherProviderHeading(String otherProviderHeading) {
        this.otherProviderHeading = otherProviderHeading;
    }

    /**
     * @return The otherAppointmentHeadingMenu
     */
    public String getOtherAppointmentHeadingMenu() {
        return otherAppointmentHeadingMenu;
    }

    /**
     * @param otherAppointmentHeadingMenu The other_appointment_heading_menu
     */
    public void setOtherAppointmentHeadingMenu(String otherAppointmentHeadingMenu) {
        this.otherAppointmentHeadingMenu = otherAppointmentHeadingMenu;
    }

    /**
     * @return The appointmentsCheckedInLabel
     */
    public String getAppointmentsCheckedInLabel() {
        return appointmentsCheckedInLabel;
    }

    /**
     * @param appointmentsCheckedInLabel The appointments_checked_in_label
     */
    public void setAppointmentsCheckedInLabel(String appointmentsCheckedInLabel) {
        this.appointmentsCheckedInLabel = appointmentsCheckedInLabel;
    }
}
