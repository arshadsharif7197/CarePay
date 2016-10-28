
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
    @SerializedName("appointments_description")
    @Expose
    private String appointmentsDescription;
    @SerializedName("add_appointment_title")
    @Expose
    private String addAppointmentTitle;
    @SerializedName("cancel_request_title")
    @Expose
    private String cancelRequestTitle;
    @SerializedName("empty_appointment_card_today_text")
    @Expose
    private String emptyAppointmentCardTodayText;
    @SerializedName("empty_appointment_card_upcoming_text")
    @Expose
    private String emptyAppointmentCardUpcomingText;
    @SerializedName("empty_appointment_card_history_text")
    @Expose
    private String emptyAppointmentCardHistoryText;
    @SerializedName("empty_appointment_card_random_text")
    @Expose
    private String emptyAppointmentCardRandomText;
    @SerializedName("add_appointment_when_empty_title")
    @Expose
    private String addAppointmentWhenEmptyTitle;
    @SerializedName("history_appointments_heading")
    @Expose
    private String historyAppointmentsHeading;
    @SerializedName("appointments_check_in_at_office")
    @Expose
    private String appointmentsCheckInAtOffice;
    @SerializedName("appointments_check_in_now")
    @Expose
    private String appointmentsCheckInNow;
    @SerializedName("appointments_place_name_heading")
    @Expose
    private String appointmentsPlaceNameHeading;
    @SerializedName("appointments_reason_for_visit_heading")
    @Expose
    private String appointmentsReasonForVisitHeading;
    @SerializedName("appointments_optional_heading")
    @Expose
    private String appointmentsOptionalHeading;
    @SerializedName("appointments_queue_heading")
    @Expose
    private String appointmentsQueueHeading;
    @SerializedName("appointments_cancel_heading")
    @Expose
    private String appointmentsCancelHeading;
    @SerializedName("appointments_canceled_heading")
    @Expose
    private String appointmentsCanceledHeading;
    @SerializedName("appointments_request_heading")
    @Expose
    private String appointmentsRequestHeading;
    @SerializedName("appointments_request_pending_heading")
    @Expose
    private String appointmentsRequestPendingHeading;
    @SerializedName("appointments_missed_heading")
    @Expose
    private String appointmentsMissedHeading;

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

    /**
     *
     * @return
     */
    public String getAppointmentsDescription() {
        return appointmentsDescription;
    }

    /**
     *
     * @param appointmentsDescription
     */
    public void setAppointmentsDescription(String appointmentsDescription) {
        this.appointmentsDescription = appointmentsDescription;
    }

    /**
     *
     * @return
     */
    public String getAddAppointmentTitle() {
        return addAppointmentTitle;
    }

    /**
     *
     * @param addAppointmentTitle
     */
    public void setAddAppointmentTitle(String addAppointmentTitle) {
        this.addAppointmentTitle = addAppointmentTitle;
    }

    /**
     *
     * @return
     */
    public String getCancelRequestTitle() {
        return cancelRequestTitle;
    }

    /**
     *
     * @param cancelRequestTitle
     */
    public void setCancelRequestTitle(String cancelRequestTitle) {
        this.cancelRequestTitle = cancelRequestTitle;
    }

    /**
     *
     * @return
     */
    public String getEmptyAppointmentCardTodayText() {
        return emptyAppointmentCardTodayText;
    }

    /**
     *
     * @param emptyAppointmentCardTodayText
     */
    public void setEmptyAppointmentCardTodayText(String emptyAppointmentCardTodayText) {
        this.emptyAppointmentCardTodayText = emptyAppointmentCardTodayText;
    }

    /**
     *
     * @return
     */
    public String getEmptyAppointmentCardUpcomingText() {
        return emptyAppointmentCardUpcomingText;
    }

    /**
     *
     * @param emptyAppointmentCardUpcomingText
     */
    public void setEmptyAppointmentCardUpcomingText(String emptyAppointmentCardUpcomingText) {
        this.emptyAppointmentCardUpcomingText = emptyAppointmentCardUpcomingText;
    }

    /**
     *
     * @return
     */
    public String getEmptyAppointmentCardHistoryText() {
        return emptyAppointmentCardHistoryText;
    }

    /**
     *
     * @param emptyAppointmentCardHistoryText
     */
    public void setEmptyAppointmentCardHistoryText(String emptyAppointmentCardHistoryText) {
        this.emptyAppointmentCardHistoryText = emptyAppointmentCardHistoryText;
    }

    /**
     *
     * @return
     */
    public String getEmptyAppointmentCardRandomText() {
        return emptyAppointmentCardRandomText;
    }

    /**
     *
     * @param emptyAppointmentCardRandomText
     */
    public void setEmptyAppointmentCardRandomText(String emptyAppointmentCardRandomText) {
        this.emptyAppointmentCardRandomText = emptyAppointmentCardRandomText;
    }

    /**
     *
     * @return
     */
    public String getAddAppointmentWhenEmptyTitle() {
        return addAppointmentWhenEmptyTitle;
    }

    /**
     *
     * @param addAppointmentWhenEmptyTitle
     */
    public void setAddAppointmentWhenEmptyTitle(String addAppointmentWhenEmptyTitle) {
        this.addAppointmentWhenEmptyTitle = addAppointmentWhenEmptyTitle;
    }

    /**
     *
     * @return
     */
    public String getHistoryAppointmentsHeading() {
        return historyAppointmentsHeading;
    }

    /**
     *
     * @param historyAppointmentsHeading
     */
    public void setHistoryAppointmentsHeading(String historyAppointmentsHeading) {
        this.historyAppointmentsHeading = historyAppointmentsHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsCheckInAtOffice() {
        return appointmentsCheckInAtOffice;
    }

    /**
     *
     * @param appointmentsCheckInAtOffice
     */
    public void setAppointmentsCheckInAtOffice(String appointmentsCheckInAtOffice) {
        this.appointmentsCheckInAtOffice = appointmentsCheckInAtOffice;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsCheckInNow() {
        return appointmentsCheckInNow;
    }

    /**
     *
     * @param appointmentsCheckInNow
     */
    public void setAppointmentsCheckInNow(String appointmentsCheckInNow) {
        this.appointmentsCheckInNow = appointmentsCheckInNow;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsPlaceNameHeading() {
        return appointmentsPlaceNameHeading;
    }

    /**
     *
     * @param appointmentsPlaceNameHeading
     */
    public void setAppointmentsPlaceNameHeading(String appointmentsPlaceNameHeading) {
        this.appointmentsPlaceNameHeading = appointmentsPlaceNameHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsReasonForVisitHeading() {
        return appointmentsReasonForVisitHeading;
    }

    /**
     *
     * @param appointmentsReasonForVisitHeading
     */
    public void setAppointmentsReasonForVisitHeading(String appointmentsReasonForVisitHeading) {
        this.appointmentsReasonForVisitHeading = appointmentsReasonForVisitHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsOptionalHeading() {
        return appointmentsOptionalHeading;
    }

    /**
     *
     * @param appointmentsOptionalHeading
     */
    public void setAppointmentsOptionalHeading(String appointmentsOptionalHeading) {
        this.appointmentsOptionalHeading = appointmentsOptionalHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsQueueHeading() {
        return appointmentsQueueHeading;
    }

    /**
     *
     * @param appointmentsQueueHeading
     */
    public void setAppointmentsQueueHeading(String appointmentsQueueHeading) {
        this.appointmentsQueueHeading = appointmentsQueueHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsCancelHeading() {
        return appointmentsCancelHeading;
    }

    /**
     *
     * @param appointmentsCancelHeading
     */
    public void setAppointmentsCancelHeading(String appointmentsCancelHeading) {
        this.appointmentsCancelHeading = appointmentsCancelHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsCanceledHeading() {
        return appointmentsCanceledHeading;
    }

    /**
     *
     * @param appointmentsCanceledHeading
     */
    public void setAppointmentsCanceledHeading(String appointmentsCanceledHeading) {
        this.appointmentsCanceledHeading = appointmentsCanceledHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsRequestHeading() {
        return appointmentsRequestHeading;
    }

    /**
     *
     * @param appointmentsRequestHeading
     */
    public void setAppointmentsRequestHeading(String appointmentsRequestHeading) {
        this.appointmentsRequestHeading = appointmentsRequestHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsRequestPendingHeading() {
        return appointmentsRequestPendingHeading;
    }

    /**
     *
     * @param appointmentsRequestPendingHeading
     */
    public void setAppointmentsRequestPendingHeading(String appointmentsRequestPendingHeading) {
        this.appointmentsRequestPendingHeading = appointmentsRequestPendingHeading;
    }

    /**
     *
     * @return
     */
    public String getAppointmentsMissedHeading() {
        return appointmentsMissedHeading;
    }

    /**
     *
     * @param appointmentsMissedHeading
     */
    public void setAppointmentsMissedHeading(String appointmentsMissedHeading) {
        this.appointmentsMissedHeading = appointmentsMissedHeading;
    }
}
