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

    public String getAppointmentRequestCheckoutNow() {
        return StringUtil.getLabelForView(appointmentRequestCheckoutNow);
    }

    public void setAppointmentRequestCheckoutNow(String appointmentRequestCheckoutNow) {
        this.appointmentRequestCheckoutNow = appointmentRequestCheckoutNow;
    }

    public String getAppointmentCancellationSuccessMessage() {
        return appointmentCancellationSuccessMessage;
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
     * @return The appointmentsRequestPendingHeading
     */
    public String getAppointmentsRequestPendingHeading() {
        return StringUtil.getLabelForView(appointmentsRequestPendingHeading);
    }

    /**
     * @return The appointmentsMissedHeading
     */
    public String getAppointmentsMissedHeading() {
        return StringUtil.getLabelForView(appointmentsMissedHeading);
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
     * @return The noAppointmentsMessageTitle
     */
    public String getNoAppointmentsMessageTitle() {
        return StringUtil.getLabelForView(noAppointmentsMessageTitle);
    }

    /**
     * @return The noAppointmentsMessageText
     */
    public String getNoAppointmentsMessageText() {
        return StringUtil.getLabelForView(noAppointmentsMessageText);
    }

    /**
     * @return The appointmentsBtnLogout
     */
    public String getAppointmentsBtnLogout() {
        return StringUtil.getLabelForView(appointmentsBtnLogout);
    }

    /**
     * @return The qrCodeErrorMessage
     */
    public String getQrCodeErrorMessage() {
        return StringUtil.getLabelForView(qrCodeErrorMessage);
    }

    /**
     * @return The cancelAppointmentReasonsTitle
     */
    public String getCancelAppointmentReasonsTitle() {
        return StringUtil.getLabelForView(cancelAppointmentReasonsTitle);
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
     * @return providerListSubHeader
     */
    public String getProviderListSubHeader() {
        return StringUtil.getLabelForView(providerListSubHeader);
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
     * @return addAppointmentMaxDateRangeMessage
     */
    public String getAddAppointmentMaxDateRangeMessage() {
        return StringUtil.getLabelForView(addAppointmentMaxDateRangeMessage);
    }

    public String getAppointmentRequestSuccessMessage() {
        return StringUtil.getLabelForView(appointmentRequestSuccessMessage);
    }

    public String getAppointmentsPreRegister() {
        return appointmentsPreRegister;
    }

    public String getAppointmentRescheduleButton() {
        return appointmentRescheduleButton;
    }
}
