package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.utils.StringUtil;
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
    @SerializedName("appointment_popup_notification_hours")
    @Expose
    private String appointmentPopupNotificationHours;
    @SerializedName("appointment_popup_notification_hour")
    @Expose
    private String appointmentPopupNotificationHour;
    @SerializedName("appointment_popup_notification_minutes")
    @Expose
    private String appointmentPopupNotificationMinutes;
    @SerializedName("appointment_popup_notification_and")
    @Expose
    private String appointmentPopupNotificationAnd;
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
    @SerializedName("appointments_check_in_at_office_button_text")
    @Expose
    private String appointmentsCheckInAtOfficeButtonText;
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
    @SerializedName("no_appointments_placeholder_label")
    @Expose
    private String noAppointmentsPlaceholderLabel;
    @SerializedName("no_appointments_message_title")
    @Expose
    private String noAppointmentsMessageTitle;
    @SerializedName("no_appointments_message_text")
    @Expose
    private String noAppointmentsMessageText;
    @SerializedName("scan_qr_code_heading")
    @Expose
    private String scanQRCodeHeading;
    @SerializedName("practice_app_logout_text")
    @Expose
    private String appointmentsBtnLogout;
    @SerializedName("practice_app_appointment_heading")
    @Expose
    private String appointmentsMainHeading;
    @SerializedName("practice_app_appointment_subheading")
    @Expose
    private String appointmentsSubHeading;
    @SerializedName("practice_app_check_in_text")
    @Expose
    private String appointmentsPracticeCheckin;

    /**
     * @return The appointmentsHeading
     */
    public String getAppointmentsHeading() {
        return StringUtil.getLabelForView(appointmentsHeading);
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
        return StringUtil.getLabelForView(cancelAppointmentsHeading);
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
        return StringUtil.getLabelForView(chooseProviderHeading);
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
        return StringUtil.getLabelForView(visitTypeHeading);
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
        return StringUtil.getLabelForView(pickDateHeading);
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
        return StringUtil.getLabelForView(availableHoursHeading);
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
        return StringUtil.getLabelForView(appointmentsCheckInEarlyPrompt);
    }

    /**
     * @param appointmentsCheckInEarlyPrompt The appointments_check_in_early_prompt
     */
    public void setAppointmentsCheckInEarlyPrompt(String appointmentsCheckInEarlyPrompt) {
        this.appointmentsCheckInEarlyPrompt = appointmentsCheckInEarlyPrompt;
    }

    /**
     * @return The appointmentPopupNotificationHours
     */
    public String getAppointmentPopupNotificationHours() {
        return StringUtil.getLabelForView(appointmentPopupNotificationHours);
    }

    /**
     * @param appointmentPopupNotificationHours The appointment_popup_notification_hours
     */
    public void setAppointmentPopupNotificationHours(String appointmentPopupNotificationHours) {
        this.appointmentPopupNotificationHours = appointmentPopupNotificationHours;
    }

    /**
     * @return The appointmentPopupNotificationHour
     */
    public String getAppointmentPopupNotificationHour() {
        return StringUtil.getLabelForView(appointmentPopupNotificationHour);
    }

    /**
     * @param appointmentPopupNotificationHour The appointment_popup_notification_hour
     */
    public void setAppointmentPopupNotificationHour(String appointmentPopupNotificationHour) {
        this.appointmentPopupNotificationHour = appointmentPopupNotificationHour;
    }

    /**
     * @return The appointmentPopupNotificationMinutes
     */
    public String getAppointmentPopupNotificationMinutes() {
        return StringUtil.getLabelForView(appointmentPopupNotificationMinutes);
    }

    /**
     * @param appointmentPopupNotificationMinutes The appointment_popup_notification_minutes
     */
    public void setAppointmentPopupNotificationMinutes(String appointmentPopupNotificationMinutes) {
        this.appointmentPopupNotificationMinutes = appointmentPopupNotificationMinutes;
    }

    /**
     * @return The appointmentPopupNotificationAnd
     */
    public String getAppointmentPopupNotificationAnd() {
        return StringUtil.getLabelForView(appointmentPopupNotificationAnd);
    }

    /**
     * @param appointmentPopupNotificationAnd The appointment_popup_notification_and
     */
    public void setAppointmentPopupNotificationAnd(String appointmentPopupNotificationAnd) {
        this.appointmentPopupNotificationAnd = appointmentPopupNotificationAnd;
    }

    /**
     * @return The dismissMessage
     */
    public String getDismissMessage() {
        return StringUtil.getLabelForView(dismissMessage);
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
        return StringUtil.getLabelForView(appointmentsCheckInEarly);
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
        return StringUtil.getLabelForView(todayAppointmentsHeading);
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
        return StringUtil.getLabelForView(upcomingAppointmentsHeading);
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
        return StringUtil.getLabelForView(missedAppointmentsHeading);
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
        return StringUtil.getLabelForView(otherProviderHeading);
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
        return StringUtil.getLabelForView(otherAppointmentHeadingMenu);
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
        return StringUtil.getLabelForView(appointmentsCheckedInLabel);
    }

    /**
     * @param appointmentsCheckedInLabel The appointments_checked_in_label
     */
    public void setAppointmentsCheckedInLabel(String appointmentsCheckedInLabel) {
        this.appointmentsCheckedInLabel = appointmentsCheckedInLabel;
    }

    /**
     * @return The appointmentsDescription
     */
    public String getAppointmentsDescription() {
        return StringUtil.getLabelForView(appointmentsDescription);
    }

    /**
     * @param appointmentsDescription The appointments_description
     */
    public void setAppointmentsDescription(String appointmentsDescription) {
        this.appointmentsDescription = appointmentsDescription;
    }

    /**
     * @return The addAppointmentTitle
     */
    public String getAddAppointmentTitle() {
        return StringUtil.getLabelForView(addAppointmentTitle);
    }

    /**
     * @param addAppointmentTitle The add_appointment_title
     */
    public void setAddAppointmentTitle(String addAppointmentTitle) {
        this.addAppointmentTitle = addAppointmentTitle;
    }

    /**
     * @return The cancelRequestTitle
     */
    public String getCancelRequestTitle() {
        return StringUtil.getLabelForView(cancelRequestTitle);
    }

    /**
     * @param cancelRequestTitle The cancel_request_title
     */
    public void setCancelRequestTitle(String cancelRequestTitle) {
        this.cancelRequestTitle = cancelRequestTitle;
    }

    /**
     * @return The emptyAppointmentCardTodayText
     */
    public String getEmptyAppointmentCardTodayText() {
        return StringUtil.getLabelForView(emptyAppointmentCardTodayText);
    }

    /**
     * @param emptyAppointmentCardTodayText The empty_appointment_card_today_text
     */
    public void setEmptyAppointmentCardTodayText(String emptyAppointmentCardTodayText) {
        this.emptyAppointmentCardTodayText = emptyAppointmentCardTodayText;
    }

    /**
     * @return The emptyAppointmentCardUpcomingText
     */
    public String getEmptyAppointmentCardUpcomingText() {
        return StringUtil.getLabelForView(emptyAppointmentCardUpcomingText);
    }

    /**
     * @param emptyAppointmentCardUpcomingText The empty_appointment_card_upcoming_text
     */
    public void setEmptyAppointmentCardUpcomingText(String emptyAppointmentCardUpcomingText) {
        this.emptyAppointmentCardUpcomingText = emptyAppointmentCardUpcomingText;
    }

    /**
     * @return The emptyAppointmentCardHistoryText
     */
    public String getEmptyAppointmentCardHistoryText() {
        return StringUtil.getLabelForView(emptyAppointmentCardHistoryText);
    }

    /**
     * @param emptyAppointmentCardHistoryText The empty_appointment_card_history_text
     */
    public void setEmptyAppointmentCardHistoryText(String emptyAppointmentCardHistoryText) {
        this.emptyAppointmentCardHistoryText = emptyAppointmentCardHistoryText;
    }

    /**
     * @return The emptyAppointmentCardRandomText
     */
    public String getEmptyAppointmentCardRandomText() {
        return StringUtil.getLabelForView(emptyAppointmentCardRandomText);
    }

    /**
     * @param emptyAppointmentCardRandomText The empty_appointment_card_random_text
     */
    public void setEmptyAppointmentCardRandomText(String emptyAppointmentCardRandomText) {
        this.emptyAppointmentCardRandomText = emptyAppointmentCardRandomText;
    }

    /**
     * @return The addAppointmentWhenEmptyTitle
     */
    public String getAddAppointmentWhenEmptyTitle() {
        return StringUtil.getLabelForView(addAppointmentWhenEmptyTitle);
    }

    /**
     * @param addAppointmentWhenEmptyTitle The add_appointment_when_empty_title
     */
    public void setAddAppointmentWhenEmptyTitle(String addAppointmentWhenEmptyTitle) {
        this.addAppointmentWhenEmptyTitle = addAppointmentWhenEmptyTitle;
    }

    /**
     * @return The historyAppointmentsHeading
     */
    public String getHistoryAppointmentsHeading() {
        return StringUtil.getLabelForView(historyAppointmentsHeading);
    }

    /**
     * @param historyAppointmentsHeading The history_appointments_heading
     */
    public void setHistoryAppointmentsHeading(String historyAppointmentsHeading) {
        this.historyAppointmentsHeading = historyAppointmentsHeading;
    }

    /**
     * @return The appointmentsCheckInAtOffice
     */
    public String getAppointmentsCheckInAtOffice() {
        return StringUtil.getLabelForView(appointmentsCheckInAtOffice);
    }

    /**
     * @param appointmentsCheckInAtOffice The appointments_check_in_at_office
     */
    public void setAppointmentsCheckInAtOffice(String appointmentsCheckInAtOffice) {
        this.appointmentsCheckInAtOffice = appointmentsCheckInAtOffice;
    }

    /**
     * @return The appointmentsCheckInNow
     */
    public String getAppointmentsCheckInNow() {
        return StringUtil.getLabelForView(appointmentsCheckInNow);
    }

    /**
     * @param appointmentsCheckInNow The appointments_check_in_now
     */
    public void setAppointmentsCheckInNow(String appointmentsCheckInNow) {
        this.appointmentsCheckInNow = appointmentsCheckInNow;
    }

    /**
     * @return The appointmentsPlaceNameHeading
     */
    public String getAppointmentsPlaceNameHeading() {
        return StringUtil.getLabelForView(appointmentsPlaceNameHeading);
    }

    /**
     * @param appointmentsPlaceNameHeading The appointments_place_name_heading
     */
    public void setAppointmentsPlaceNameHeading(String appointmentsPlaceNameHeading) {
        this.appointmentsPlaceNameHeading = appointmentsPlaceNameHeading;
    }

    /**
     * @return The appointmentsReasonForVisitHeading
     */
    public String getAppointmentsReasonForVisitHeading() {
        return StringUtil.getLabelForView(appointmentsReasonForVisitHeading);
    }

    /**
     * @param appointmentsReasonForVisitHeading The appointments_reason_for_visit_heading
     */
    public void setAppointmentsReasonForVisitHeading(String appointmentsReasonForVisitHeading) {
        this.appointmentsReasonForVisitHeading = appointmentsReasonForVisitHeading;
    }

    /**
     * @return The appointmentsOptionalHeading
     */
    public String getAppointmentsOptionalHeading() {
        return StringUtil.getLabelForView(appointmentsOptionalHeading);
    }

    /**
     * @param appointmentsOptionalHeading The appointments_optional_heading
     */
    public void setAppointmentsOptionalHeading(String appointmentsOptionalHeading) {
        this.appointmentsOptionalHeading = appointmentsOptionalHeading;
    }

    /**
     * @return The appointmentsQueueHeading
     */
    public String getAppointmentsQueueHeading() {
        return StringUtil.getLabelForView(appointmentsQueueHeading);
    }

    /**
     * @param appointmentsQueueHeading The appointments_queue_heading
     */
    public void setAppointmentsQueueHeading(String appointmentsQueueHeading) {
        this.appointmentsQueueHeading = appointmentsQueueHeading;
    }

    /**
     * @return The appointmentsCancelHeading
     */
    public String getAppointmentsCancelHeading() {
        return StringUtil.getLabelForView(appointmentsCancelHeading);
    }

    /**
     * @param appointmentsCancelHeading The appointments_cancel_heading
     */
    public void setAppointmentsCancelHeading(String appointmentsCancelHeading) {
        this.appointmentsCancelHeading = appointmentsCancelHeading;
    }

    /**
     * @return The appointmentsCanceledHeading
     */
    public String getAppointmentsCanceledHeading() {
        return StringUtil.getLabelForView(appointmentsCanceledHeading);
    }

    /**
     * @param appointmentsCanceledHeading The appointments_canceled_heading
     */
    public void setAppointmentsCanceledHeading(String appointmentsCanceledHeading) {
        this.appointmentsCanceledHeading = appointmentsCanceledHeading;
    }

    /**
     * @return The appointmentsRequestHeading
     */
    public String getAppointmentsRequestHeading() {
        return StringUtil.getLabelForView(appointmentsRequestHeading);
    }

    /**
     * @param appointmentsRequestHeading The appointments_request_heading
     */
    public void setAppointmentsRequestHeading(String appointmentsRequestHeading) {
        this.appointmentsRequestHeading = appointmentsRequestHeading;
    }

    /**
     * @return The appointmentsRequestPendingHeading
     */
    public String getAppointmentsRequestPendingHeading() {
        return StringUtil.getLabelForView(appointmentsRequestPendingHeading);
    }

    /**
     * @param appointmentsRequestPendingHeading The appointments_request_pending_heading
     */
    public void setAppointmentsRequestPendingHeading(String appointmentsRequestPendingHeading) {
        this.appointmentsRequestPendingHeading = appointmentsRequestPendingHeading;
    }

    /**
     * @return The appointmentsMissedHeading
     */
    public String getAppointmentsMissedHeading() {
        return StringUtil.getLabelForView(appointmentsMissedHeading);
    }

    /**
     * @param appointmentsMissedHeading The appointments_missed_heading
     */
    public void setAppointmentsMissedHeading(String appointmentsMissedHeading) {
        this.appointmentsMissedHeading = appointmentsMissedHeading;
    }

    /**
     * @return The scanQRCodeHeading
     */
    public String getScanQRCodeHeading() {
        return StringUtil.getLabelForView(scanQRCodeHeading);
    }

    /**
     * @param scanQRCodeHeading The scan_qr_code_heading
     */
    public void setScanQRCodeHeading(String scanQRCodeHeading) {
        this.scanQRCodeHeading = scanQRCodeHeading;
    }

    /**
     * @return The appointmentsCheckInAtOfficeButtonText
     */
    public String getAppointmentsCheckInAtOfficeButtonText() {
        return StringUtil.getLabelForView(appointmentsCheckInAtOfficeButtonText);
    }

    /**
     * @param appointmentsCheckInAtOfficeButtonText The appointments_check_in_at_office_button_text
     */
    public void setAppointmentsCheckInAtOfficeButtonText(String appointmentsCheckInAtOfficeButtonText) {
        this.appointmentsCheckInAtOfficeButtonText = appointmentsCheckInAtOfficeButtonText;
    }

    /**
     * @return The noAppointmentsPlaceholderLabel
     */
    public String getNoAppointmentsPlaceholderLabel() {
        return StringUtil.getLabelForView(noAppointmentsPlaceholderLabel);
    }

    /**
     * @param noAppointmentsPlaceholderLabel The no_appointments_placeholder_label
     */
    public void setNoAppointmentsPlaceholderLabel(String noAppointmentsPlaceholderLabel) {
        this.noAppointmentsPlaceholderLabel = noAppointmentsPlaceholderLabel;
    }

    /**
     * @return The noAppointmentsMessageTitle
     */
    public String getNoAppointmentsMessageTitle() {
        return StringUtil.getLabelForView(noAppointmentsMessageTitle);
    }

    /**
     * @param noAppointmentsMessageTitle The no_appointments_message_title
     */
    public void setNoAppointmentsMessageTitle(String noAppointmentsMessageTitle) {
        this.noAppointmentsMessageTitle = noAppointmentsMessageTitle;
    }

    /**
     * @return The noAppointmentsMessageText
     */
    public String getNoAppointmentsMessageText() {
        return StringUtil.getLabelForView(noAppointmentsMessageText);
    }

    /**
     * @param noAppointmentsMessageText The no_appointments_message_text
     */
    public void setNoAppointmentsMessageText(String noAppointmentsMessageText) {
        this.noAppointmentsMessageText = noAppointmentsMessageText;
    }

    /**
     * @return The appointmentsBtnLogout
     */
    public String getAppointmentsBtnLogout() {
        return StringUtil.getLabelForView(appointmentsBtnLogout);
    }

    /**
     * @param appointmentsBtnLogout The appointmentsBtnLogout
     */
    public void setAppointmentsBtnLogout(String appointmentsBtnLogout) {
        this.appointmentsBtnLogout = appointmentsBtnLogout;
    }

    /**
     * @return The appointmentsMainHeading
     */
    public String getAppointmentsMainHeading() {
        return StringUtil.getLabelForView(appointmentsMainHeading);
    }

    /**
     * @param appointmentsMainHeading The appointmentsMainHeading
     */
    public void setAppointmentsMainHeading(String appointmentsMainHeading) {
        this.appointmentsMainHeading = appointmentsMainHeading;
    }

    /**
     * @return The appointmentsSubHeading
     */
    public String getAppointmentsSubHeading() {
        return StringUtil.getLabelForView(appointmentsSubHeading);
    }

    /**
     * @param appointmentsSubHeading The appointmentsSubHeading
     */
    public void setAppointmentsSubHeading(String appointmentsSubHeading) {
        this.appointmentsSubHeading = appointmentsSubHeading;
    }

    /**
     * @return The appointmentsPracticeCheckin
     */
    public String getAppointmentsPracticeCheckin() {
        return StringUtil.getLabelForView(appointmentsPracticeCheckin);
    }

    /**
     * @param appointmentsPracticeCheckin The appointmentsPracticeCheckin
     */
    public void setAppointmentsPracticeCheckin(String appointmentsPracticeCheckin) {
        this.appointmentsPracticeCheckin = appointmentsPracticeCheckin;
    }
}
