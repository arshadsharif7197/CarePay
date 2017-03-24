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
    @SerializedName("appointments_web_today_heading")
    @Expose
    private String appointmentsTodayHeadingSmall;
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
    @SerializedName("qr_code_error_message")
    @Expose
    private String qrCodeErrorMessage;
    @SerializedName("cancel_appointment_reasons_title")
    @Expose
    private String cancelAppointmentReasonsTitle;
    @SerializedName("cancel_appointment_other_reason_label")
    @Expose
    private String cancelAppointmentOtherReasonLabel;
    @SerializedName("cancel_appointment_other_reason_hint")
    @Expose
    private String cancelAppointmentOtherReasonHint;
    @SerializedName("choose_provider_recent_header")
    @Expose
    private String chooseProviderRecentHeader;
    @SerializedName("provider_list_header")
    @Expose
    private String providerListHeader;
    @SerializedName("provider_list_sub_header")
    @Expose
    private String providerListSubHeader;
    @SerializedName("provider_list_schedule_appointment_button")
    @Expose
    private String providerListScheduleAppointmentButton;
    @SerializedName("available_hours_back")
    @Expose
    private String availableHoursBack;
    @SerializedName("request_appointment_new_patient")
    @Expose
    private String requestAppointmentNewPatient;
    @SerializedName("add_appointment_from_to_text")
    @Expose
    private String addAppointmentFromToText;
    @SerializedName("add_appointment_tomorrow")
    @Expose
    private String addAppointmentTomorrow;
    @SerializedName("add_appointment_max_date_range_message")
    @Expose
    private String addAppointmentMaxDateRangeMessage;
    @SerializedName("appointment_request_success_message_HTML")
    @Expose
    private String appointmentRequestSuccessMessage;
    @SerializedName("no_shop_message_title")
    @Expose
    private String noShopMessageTitle;
    @SerializedName("no_shop_message_text")
    @Expose
    private String noShopMessageText;
    @SerializedName("no_notifications_message_title")
    @Expose
    private String noNotificationsMessageTitle;
    @SerializedName("no_notifications_message_text")
    @Expose
    private String noNotificationsMessageText;
    @SerializedName("appointment_select_range_button")
    @Expose
    private String appointmentSelectRangeButton;
    @SerializedName("appointment_next_days_title")
    @Expose
    private String appointmentNextDaysTitle;
    @SerializedName("appointment_locations_label")
    @Expose
    private String appointmentLocationsLabel;
    @SerializedName("appoitment_edit_date_range_button")
    @Expose
    private String appointmentEditDateRangeButton;

    @SerializedName("datepicker_cancel_option")
    @Expose
    private String datepickerCancelOption;

    @SerializedName("appointments_pre_register")
    @Expose
    private String appointmentsPreRegister;

    @SerializedName("appointment_cancellation_success_message_HTML")
    @Expose
    private String appointmentCancellationSuccessMessage;

    @SerializedName("appointment_request_checkout_now")
    @Expose
    private String appointmentRequestCheckoutNow;

    @SerializedName("appointment_reschedule_button")
    @Expose
    private String appointmentRescheduleButton;

    @SerializedName("appointment_schedule_new_button")
    @Expose
    private String appointmentScheduleNewButton;

    @SerializedName("practice_list_select_a_provider")
    @Expose
    private String practiceListSelectProvider;

    @SerializedName("practice_list_continue")
    @Expose
    private String practiceListContinue;

    public String getAppointmentRequestCheckoutNow() {
        return StringUtil.getLabelForView(appointmentRequestCheckoutNow);
    }

    public void setAppointmentRequestCheckoutNow(String appointmentRequestCheckoutNow) {
        this.appointmentRequestCheckoutNow = appointmentRequestCheckoutNow;
    }

    public String getAppointmentCancellationSuccessMessage() {
        return appointmentCancellationSuccessMessage;
    }

    public void setAppointmentCancellationSuccessMessage(String appointmentCancellationSuccessMessage) {
        this.appointmentCancellationSuccessMessage = appointmentCancellationSuccessMessage;
    }

    public String getAppointmentSelectRangeButton() {
        return appointmentSelectRangeButton;
    }

    public void setAppointmentSelectRangeButton(String appointmentSelectRangeButton) {
        this.appointmentSelectRangeButton = appointmentSelectRangeButton;
    }

    public String getAppointmentNextDaysTitle() {
        return appointmentNextDaysTitle;
    }

    public void setAppointmentNextDaysTitle(String appointmentNextDaysTitle) {
        this.appointmentNextDaysTitle = appointmentNextDaysTitle;
    }

    public String getNoShopMessageTitle() {
        return noShopMessageTitle;
    }

    public String getNoShopMessageText() {
        return noShopMessageText;
    }

    public String getNoNotificationsMessageTitle() {
        return noNotificationsMessageTitle;
    }

    public String getNoNotificationsMessageText() {
        return noNotificationsMessageText;
    }

    /**
     * @return The appointmentsHeading
     */
    public String getAppointmentsHeading() {
        return StringUtil.getLabelForView(appointmentsHeading);
    }

    /**
     * @return The cancelAppointmentsHeading
     */
    public String getCancelAppointmentsHeading() {
        return StringUtil.getLabelForView(cancelAppointmentsHeading);
    }

    /**
     * @return The chooseProviderHeading
     */
    public String getChooseProviderHeading() {
        return StringUtil.getLabelForView(chooseProviderHeading);
    }

    /**
     * @return The visitTypeHeading
     */
    public String getVisitTypeHeading() {
        return StringUtil.getLabelForView(visitTypeHeading);
    }

    /**
     * @return The pickDateHeading
     */
    public String getPickDateHeading() {
        return StringUtil.getLabelForView(pickDateHeading);
    }

    /**
     * @return The availableHoursHeading
     */
    public String getAvailableHoursHeading() {
        return StringUtil.getLabelForView(availableHoursHeading);
    }

    /**
     * @return The appointmentsCheckInEarly
     */
    public String getAppointmentsCheckInEarly() {
        return StringUtil.getLabelForView(appointmentsCheckInEarly);
    }

    /**
     * @return The todayAppointmentsHeading;
     */
    public String getTodayAppointmentsHeading() {
        return StringUtil.getLabelForView(todayAppointmentsHeading);
    }

    /**
     * @return The upcomingAppointmentsHeading
     */
    public String getUpcomingAppointmentsHeading() {
        return StringUtil.getLabelForView(upcomingAppointmentsHeading);
    }

    /**
     * @return The missedAppointmentsHeading
     */
    public String getMissedAppointmentsHeading() {
        return StringUtil.getLabelForView(missedAppointmentsHeading);
    }

    /**
     * @return The appointmentsCheckedInLabel
     */
    public String getAppointmentsCheckedInLabel() {
        return StringUtil.getLabelForView(appointmentsCheckedInLabel);
    }

    /**
     * @return The appointmentsCheckInAtOffice
     */
    public String getAppointmentsCheckInAtOffice() {
        return StringUtil.getLabelForView(appointmentsCheckInAtOffice);
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
     * @return The appointmentsCancelHeading
     */
    public String getAppointmentsCancelHeading() {
        return StringUtil.getLabelForView(appointmentsCancelHeading);
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
     * @return The appointmentsCheckInAtOfficeButtonText
     */
    public String getAppointmentsCheckInAtOfficeButtonText() {
        return StringUtil.getLabelForView(appointmentsCheckInAtOfficeButtonText);
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

    /**
     * @return The qrCodeErrorMessage
     */
    public String getQrCodeErrorMessage() {
        return StringUtil.getLabelForView(qrCodeErrorMessage);
    }

    /**
     * @param qrCodeErrorMessage The qrCodeErrorMessage
     */
    public void setQrCodeErrorMessage(String qrCodeErrorMessage) {
        this.qrCodeErrorMessage = qrCodeErrorMessage;
    }

    /**
     * @return The cancelAppointmentReasonsTitle
     */
    public String getCancelAppointmentReasonsTitle() {
        return StringUtil.getLabelForView(cancelAppointmentReasonsTitle);
    }

    /**
     * @param cancelAppointmentReasonsTitle The cancel_appointment_reasons_title
     */
    public void setCancelAppointmentReasonsTitle(String cancelAppointmentReasonsTitle) {
        this.cancelAppointmentReasonsTitle = cancelAppointmentReasonsTitle;
    }

    /**
     * @return The cancelAppointmentOtherReasonLabel
     */
    public String getCancelAppointmentOtherReasonLabel() {
        return StringUtil.getLabelForView(cancelAppointmentOtherReasonLabel);
    }

    /**
     * @param cancelAppointmentOtherReasonLabel The cancel_appointment_other_reason_label
     */
    public void setCancelAppointmentOtherReasonLabel(String cancelAppointmentOtherReasonLabel) {
        this.cancelAppointmentOtherReasonLabel = cancelAppointmentOtherReasonLabel;
    }

    /**
     *
     * @return The cancelAppointmentOtherReasonHint
     */
    public String getCancelAppointmentOtherReasonHint() {
        return StringUtil.getLabelForView(cancelAppointmentOtherReasonHint);
    }

    /**
     *
     * @return providerListHeader
     */
    public String getProviderListHeader() {
        return StringUtil.getLabelForView(providerListHeader);
    }

    /**
     *
     * @param providerListHeader providerListHeader
     */
    public void setProviderListHeader(String providerListHeader) {
        this.providerListHeader = providerListHeader;
    }

    /**
     *
     * @return providerListSubHeader
     */
    public String getProviderListSubHeader() {
        return StringUtil.getLabelForView(providerListSubHeader);
    }

    /**
     *
     * @param providerListSubHeader providerListSubHeader
     */
    public void setProviderListSubHeader(String providerListSubHeader) {
        this.providerListSubHeader = providerListSubHeader;
    }

    /**
     *
     * @return providerListScheduleAppointmentButton
     */
    public String getProviderListScheduleAppointmentButton() {
        return StringUtil.getLabelForView(providerListScheduleAppointmentButton);
    }

    /**
     *
     * @param providerListScheduleAppointmentButton providerListScheduleAppointmentButton
     */
    public void setProviderListScheduleAppointmentButton(String providerListScheduleAppointmentButton) {
        this.providerListScheduleAppointmentButton = providerListScheduleAppointmentButton;
    }

    /**
     *
     * @return appointmentsTodayHeadingSmall
     */
    public String getAppointmentsTodayHeadingSmall() {
        return StringUtil.getLabelForView(appointmentsTodayHeadingSmall);
    }

    /**
     *
     * @param appointmentsTodayHeadingSmall appointmentsTodayHeadingSmall
     */
    public void setAppointmentsTodayHeadingSmall(String appointmentsTodayHeadingSmall) {
        this.appointmentsTodayHeadingSmall = appointmentsTodayHeadingSmall;
    }

    /**
     * Gets available hours back.
     *
     * @return the available hours back
     */
    public String getAvailableHoursBack() {
        return StringUtil.getLabelForView(availableHoursBack);
    }

    /**
     * Sets available hours back.
     *
     * @param availableHoursBack the available hours back
     */
    public void setAvailableHoursBack(String availableHoursBack) {
        this.availableHoursBack = availableHoursBack;
    }

    /**
     * Gets request appointment new patient.
     *
     * @return the request appointment new patient
     */
    public String getRequestAppointmentNewPatient() {
        return StringUtil.getLabelForView(requestAppointmentNewPatient);
    }

    /**
     * Sets request appointment new patient.
     *
     * @param requestAppointmentNewPatient the request appointment new patient
     */
    public void setRequestAppointmentNewPatient(String requestAppointmentNewPatient) {
        this.requestAppointmentNewPatient = requestAppointmentNewPatient;
    }

    /**
     * Gets add appointment from to text.
     *
     * @return the add appointment from to text
     */
    public String getAddAppointmentFromToText() {
        return StringUtil.getLabelForView(addAppointmentFromToText);
    }

    /**
     * Gets add appointment tomorrow.
     *
     * @return the add appointment tomorrow
     */
    public String getAddAppointmentTomorrow() {
        return StringUtil.getLabelForView(addAppointmentTomorrow);
    }

    /**
     *
     * @return addAppointmentMaxDateRangeMessage
     */
    public String getAddAppointmentMaxDateRangeMessage() {
        return StringUtil.getLabelForView(addAppointmentMaxDateRangeMessage);
    }

    public String getAppointmentRequestSuccessMessage() {
        return StringUtil.getLabelForView(appointmentRequestSuccessMessage);
    }

    public String getAppointmentLocationsLabel() {
        return appointmentLocationsLabel;
    }

    public String getAppointmentEditDateRangeButton() {
        return appointmentEditDateRangeButton;
    }

    public String getDatepickerCancelOption() {
        return datepickerCancelOption;
    }

    public String getAppointmentsPreRegister() {
        return appointmentsPreRegister;
    }

    public String getAppointmentRescheduleButton() {
        return appointmentRescheduleButton;
    }

    public String getPracticeListSelectProvider() {
        return StringUtil.getLabelForView(practiceListSelectProvider);
    }

    public String getPracticeListContinue() {
        return StringUtil.getLabelForView(practiceListContinue);
    }
}
