package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepaylibray.appointments.models.AppointmentLabelDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */
@Deprecated
public class CheckInLabelDTO extends AppointmentLabelDTO {

    @SerializedName("practice_checkin_detail_dialog_checking_in")
    @Expose
    private String practiceCheckinDetailDialogCheckingIn;
    @SerializedName("practice_checkin_detail_dialog_balance")
    @Expose
    private String practiceCheckinDetailDialogBalance;
    @SerializedName("practice_checkin_detail_dialog_demographics")
    @Expose
    private String practiceCheckinDetailDialogDemographics;
    @SerializedName("practice_checkin_detail_dialog_consent_forms")
    @Expose
    private String practiceCheckinDetailDialogConsentForms;
    @SerializedName("practice_checkin_detail_dialog_intake")
    @Expose
    private String practiceCheckinDetailDialogIntake;
    @SerializedName("practice_checkin_detail_dialog_responsibility")
    @Expose
    private String practiceCheckinDetailDialogResponsibility;
    @SerializedName("practice_checkin_detail_dialog_payment")
    @Expose
    private String practiceCheckinDetailDialogPayment;
    @SerializedName("practice_checkin_detail_dialog_assist")
    @Expose
    private String practiceCheckinDetailDialogAssist;
    @SerializedName("practice_checkin_detail_dialog_page")
    @Expose
    private String practiceCheckinDetailDialogPage;
    @SerializedName("practice_checkin_filter")
    @Expose
    private String practiceCheckinFilter;
    @SerializedName("practice_checkin_filter_On")
    @Expose
    private String practiceCheckinFilterOn;
    @SerializedName("practice_checkin_filter_find_patient_by_name")
    @Expose
    private String practiceCheckinFilterFindPatientByName;
    @SerializedName("practice_checkin_filter_clear_filters")
    @Expose
    private String practiceCheckinFilterClearFilters;
    @SerializedName("practice_checkin_detail_dialog_ordinal_th")
    @Expose
    private String practiceCheckinDetailDialogOrdinalTh;
    @SerializedName("practice_checkin_detail_dialog_ordinal_st")
    @Expose
    private String practiceCheckinDetailDialogOrdinalSt;
    @SerializedName("practice_checkin_detail_dialog_ordinal_nd")
    @Expose
    private String practiceCheckinDetailDialogOrdinalNd;
    @SerializedName("practice_checkin_detail_dialog_ordinal_rd")
    @Expose
    private String practiceCheckinDetailDialogOrdinalRd;
    @SerializedName("practice_checkin_detail_dialog_waiting_room")
    @Expose
    private String practiceCheckinDetailDialogWaitingRoom;
    @SerializedName("practice_checkin_detail_dialog_in_queue")
    @Expose
    private String practiceCheckinDetailDialogQueue;
    @SerializedName("practice_checkin_waiting_room")
    @Expose
    private String practiceCheckinWaitingRoom;
    @SerializedName("go_back")
    @Expose
    private String goBack;
    @SerializedName("practice_checkin_drop_here_label")
    @Expose
    private String practiceCheckinDropHereLabel;

    @SerializedName("practice_checkin_success_label")
    @Expose
    private String practiceCheckinSuccessLabel;

    @SerializedName("activity_heading")
    @Expose
    private String activityHeading;

    @SerializedName("today_label")
    @Expose
    private String todayLabel;

    @SerializedName("date_range_picker_dialog_title")
    @Expose
    private String dateRangePickerDialogTitle;

    @SerializedName("date_range_picker_dialog_close")
    @Expose
    private String dateRangePickerDialogClose;

    @SerializedName("change_date_range_label")
    @Expose
    private String changeDateRangeLabel;

    @SerializedName("tomorrow_label")
    @Expose
    private String tomorrowLabel;

    @SerializedName("this_month_label")
    @Expose
    private String thisMonthLabel;

    @SerializedName("next_days_label")
    @Expose
    private String nextDaysLabel;

    @SerializedName("all_appointments_label")
    @Expose
    private String allAppointmentsLabel;

    @SerializedName("pending_appointments_label")
    @Expose
    private String pendingAppointmentsLabel;

    @SerializedName("pending_label")
    @Expose
    private String pendingLabel;

    @SerializedName("practice_checkin_filter_find_patient")
    @Expose
    private String practiceCheckinFilterFindPatient;

    @SerializedName("practice_checkin_filter_find_patient_first")
    @Expose
    private String practiceCheckinFilterFindPatientFirst;

    @SerializedName("practice_checkin_filter_search_patient")
    @Expose
    private String practiceCheckinFilterSearchPatient;

    @SerializedName("practice_checkin_close_label")
    @Expose
    private String practiceCheckinCloseLabel;

    @SerializedName("create_appointment_label")
    @Expose
    private String createAppointmentLabel;

    @SerializedName("cancel_appointment_label")
    @Expose
    private String cancelAppointmentLabel;

    @SerializedName("accept_label")
    @Expose
    private String acceptLabel;

    @SerializedName("reject_label")
    @Expose
    private String rejectLabel;

    /**
     * @return The practiceCheckinDetailDialogCheckingIn
     */
    public String getPracticeCheckinDetailDialogCheckingIn() {
        return StringUtil.getLabelForView(practiceCheckinDetailDialogCheckingIn);
    }

    /**
     * @param practiceCheckinDetailDialogCheckingIn The practice_checkin_detail_dialog_checking_in
     */
    public void setPracticeCheckinDetailDialogCheckingIn(String practiceCheckinDetailDialogCheckingIn) {
        this.practiceCheckinDetailDialogCheckingIn = practiceCheckinDetailDialogCheckingIn;
    }

    /**
     * @return The practiceCheckinDetailDialogBalance
     */
    public String getPracticeCheckinDetailDialogBalance() {
        return practiceCheckinDetailDialogBalance;
    }

    /**
     * @param practiceCheckinDetailDialogBalance The practice_checkin_detail_dialog_balance
     */
    public void setPracticeCheckinDetailDialogBalance(String practiceCheckinDetailDialogBalance) {
        this.practiceCheckinDetailDialogBalance = practiceCheckinDetailDialogBalance;
    }

    /**
     * @return The practiceCheckinDetailDialogDemographics
     */
    public String getPracticeCheckinDetailDialogDemographics() {
        return practiceCheckinDetailDialogDemographics;
    }

    /**
     * @param practiceCheckinDetailDialogDemographics The practice_checkin_detail_dialog_demographics
     */
    public void setPracticeCheckinDetailDialogDemographics(String practiceCheckinDetailDialogDemographics) {
        this.practiceCheckinDetailDialogDemographics = practiceCheckinDetailDialogDemographics;
    }

    /**
     * @return The practiceCheckinDetailDialogConsentForms
     */
    public String getPracticeCheckinDetailDialogConsentForms() {
        return practiceCheckinDetailDialogConsentForms;
    }

    /**
     * @param practiceCheckinDetailDialogConsentForms The practice_checkin_detail_dialog_consent_forms
     */
    public void setPracticeCheckinDetailDialogConsentForms(String practiceCheckinDetailDialogConsentForms) {
        this.practiceCheckinDetailDialogConsentForms = practiceCheckinDetailDialogConsentForms;
    }

    /**
     * @return The practiceCheckinDetailDialogIntake
     */
    public String getPracticeCheckinDetailDialogIntake() {
        return practiceCheckinDetailDialogIntake;
    }

    /**
     * @param practiceCheckinDetailDialogIntake The practice_checkin_detail_dialog_intake
     */
    public void setPracticeCheckinDetailDialogIntake(String practiceCheckinDetailDialogIntake) {
        this.practiceCheckinDetailDialogIntake = practiceCheckinDetailDialogIntake;
    }

    /**
     * @return The practiceCheckinDetailDialogResponsibility
     */
    public String getPracticeCheckinDetailDialogResponsibility() {
        return practiceCheckinDetailDialogResponsibility;
    }

    /**
     * @param practiceCheckinDetailDialogResponsibility The practice_checkin_detail_dialog_responsibility
     */
    public void setPracticeCheckinDetailDialogResponsibility(String practiceCheckinDetailDialogResponsibility) {
        this.practiceCheckinDetailDialogResponsibility = practiceCheckinDetailDialogResponsibility;
    }

    /**
     * @return The practiceCheckinDetailDialogPayment
     */
    public String getPracticeCheckinDetailDialogPayment() {
        return practiceCheckinDetailDialogPayment;
    }

    /**
     * @param practiceCheckinDetailDialogPayment The practice_checkin_detail_dialog_payment
     */
    public void setPracticeCheckinDetailDialogPayment(String practiceCheckinDetailDialogPayment) {
        this.practiceCheckinDetailDialogPayment = practiceCheckinDetailDialogPayment;
    }

    /**
     * @return The practiceCheckinDetailDialogAssist
     */
    public String getPracticeCheckinDetailDialogAssist() {
        return practiceCheckinDetailDialogAssist;
    }

    /**
     * @param practiceCheckinDetailDialogAssist The practice_checkin_detail_dialog_assist
     */
    public void setPracticeCheckinDetailDialogAssist(String practiceCheckinDetailDialogAssist) {
        this.practiceCheckinDetailDialogAssist = practiceCheckinDetailDialogAssist;
    }

    /**
     * @return The practiceCheckinDetailDialogPage
     */
    public String getPracticeCheckinDetailDialogPage() {
        return practiceCheckinDetailDialogPage;
    }

    /**
     * @param practiceCheckinDetailDialogPage The practice_checkin_detail_dialog_page
     */
    public void setPracticeCheckinDetailDialogPage(String practiceCheckinDetailDialogPage) {
        this.practiceCheckinDetailDialogPage = practiceCheckinDetailDialogPage;
    }

    /**
     * @return The practiceCheckinFilter
     */
    public String getPracticeCheckinFilter() {
        return StringUtil.getLabelForView(practiceCheckinFilter);
    }

    /**
     * @param practiceCheckinFilter The practice_checkin_filter
     */
    public void setPracticeCheckinFilter(String practiceCheckinFilter) {
        this.practiceCheckinFilter = practiceCheckinFilter;
    }

    /**
     * @return The practiceCheckinFilterFindPatientByName
     */
    public String getPracticeCheckinFilterFindPatientByName() {
        return practiceCheckinFilterFindPatientByName;
    }

    /**
     * @param practiceCheckinFilterFindPatientByName The practice_checkin_filter_find_patient_by_name
     */
    public void setPracticeCheckinFilterFindPatientByName(String practiceCheckinFilterFindPatientByName) {
        this.practiceCheckinFilterFindPatientByName = practiceCheckinFilterFindPatientByName;
    }

    /**
     * @return The practiceCheckinFilterClearFilters
     */
    public String getPracticeCheckinFilterClearFilters() {
        return practiceCheckinFilterClearFilters;
    }

    /**
     * @param practiceCheckinFilterClearFilters The practice_checkin_filter_clear_filters
     */
    public void setPracticeCheckinFilterClearFilters(String practiceCheckinFilterClearFilters) {
        this.practiceCheckinFilterClearFilters = practiceCheckinFilterClearFilters;
    }

    public String getPracticeCheckinWaitingRoom() {
        return StringUtil.getLabelForView(practiceCheckinWaitingRoom);
    }

    public void setPracticeCheckinWaitingRoom(String practiceCheckinWaitingRoom) {
        this.practiceCheckinWaitingRoom = practiceCheckinWaitingRoom;
    }

    public String getGoBack() {
        return StringUtil.getLabelForView(goBack);
    }

    public void setGoBack(String goBack) {
        this.goBack = goBack;
    }

    public String getPracticeCheckinFilterOn() {
        return StringUtil.getLabelForView(practiceCheckinFilterOn);
    }

    public void setPracticeCheckinFilterOn(String practiceCheckinFilterOn) {
        this.practiceCheckinFilterOn = practiceCheckinFilterOn;
    }

    public String getPracticeCheckinDetailDialogOrdinalTh() {
        return practiceCheckinDetailDialogOrdinalTh;
    }

    public void setPracticeCheckinDetailDialogOrdinalTh(String practiceCheckinDetailDialogOrdinalTh) {
        this.practiceCheckinDetailDialogOrdinalTh = practiceCheckinDetailDialogOrdinalTh;
    }

    public String getPracticeCheckinDetailDialogOrdinalSt() {
        return practiceCheckinDetailDialogOrdinalSt;
    }

    public void setPracticeCheckinDetailDialogOrdinalSt(String practiceCheckinDetailDialogOrdinalSt) {
        this.practiceCheckinDetailDialogOrdinalSt = practiceCheckinDetailDialogOrdinalSt;
    }

    public String getPracticeCheckinDetailDialogOrdinalNd() {
        return practiceCheckinDetailDialogOrdinalNd;
    }

    public void setPracticeCheckinDetailDialogOrdinalNd(String practiceCheckinDetailDialogOrdinalNd) {
        this.practiceCheckinDetailDialogOrdinalNd = practiceCheckinDetailDialogOrdinalNd;
    }

    public String getPracticeCheckinDetailDialogOrdinalRd() {
        return practiceCheckinDetailDialogOrdinalRd;
    }

    public void setPracticeCheckinDetailDialogOrdinalRd(String practiceCheckinDetailDialogOrdinalRd) {
        this.practiceCheckinDetailDialogOrdinalRd = practiceCheckinDetailDialogOrdinalRd;
    }

    public String getPracticeCheckinDetailDialogWaitingRoom() {
        return practiceCheckinDetailDialogWaitingRoom;
    }

    public void setPracticeCheckinDetailDialogWaitingRoom(String practiceCheckinDetailDialogWaitingRoom) {
        this.practiceCheckinDetailDialogWaitingRoom = practiceCheckinDetailDialogWaitingRoom;
    }

    public String getPracticeCheckinDetailDialogQueue() {
        return practiceCheckinDetailDialogQueue;
    }

    public void setPracticeCheckinDetailDialogQueue(String practiceCheckinDetailDialogQueue) {
        this.practiceCheckinDetailDialogQueue = practiceCheckinDetailDialogQueue;
    }

    public String getPracticeCheckinDropHereLabel() {
        return StringUtil.getLabelForView(practiceCheckinDropHereLabel);
    }

    public void setPracticeCheckinDropHereLabel(String practiceCheckinDropHereLabel) {
        this.practiceCheckinDropHereLabel = practiceCheckinDropHereLabel;
    }

    public String getPracticeCheckinSuccessLabel() {
        return StringUtil.getLabelForView(practiceCheckinSuccessLabel);
    }

    public void setPracticeCheckinSuccessLabel(String practiceCheckinSuccessLabel) {
        this.practiceCheckinSuccessLabel = practiceCheckinSuccessLabel;
    }

    public String getActivityHeading() {
        return StringUtil.getLabelForView(activityHeading);
    }

    public String getTodayLabel() {
        return StringUtil.getLabelForView(todayLabel);
    }

    public String getDateRangePickerDialogTitle() {
        return StringUtil.getLabelForView(dateRangePickerDialogTitle);
    }

    public String getDateRangePickerDialogClose() {
        return StringUtil.getLabelForView(dateRangePickerDialogClose);
    }

    public String getChangeDateRangeLabel() {
        return StringUtil.getLabelForView(changeDateRangeLabel);
    }

    public String getTomorrowLabel() {
        return StringUtil.getLabelForView(tomorrowLabel);
    }

    public String getThisMonthLabel() {
        return StringUtil.getLabelForView(thisMonthLabel);
    }

    public String getNextDaysLabel() {
        return StringUtil.getLabelForView(nextDaysLabel);
    }

    public String getAllAppointmentsLabel() {
        return StringUtil.getLabelForView(allAppointmentsLabel);
    }

    public String getPendingAppointmentsLabel() {
        return StringUtil.getLabelForView(pendingAppointmentsLabel);
    }

    public String getPendingLabel() {
        return StringUtil.getLabelForView(pendingLabel);
    }

    public String getPracticeCheckinFilterFindPatient() {
        return StringUtil.getLabelForView(practiceCheckinFilterFindPatient);
    }

    public String getPracticeCheckinFilterFindPatientFirst() {
        return practiceCheckinFilterFindPatientFirst;
    }

    public String getPracticeCheckinFilterSearchPatient() {
        return practiceCheckinFilterSearchPatient;
    }

    public String getPracticeCheckinCloseLabel() {
        return StringUtil.getLabelForView(practiceCheckinCloseLabel);
    }

    public String getCreateAppointmentLabel() {
        return StringUtil.getLabelForView(createAppointmentLabel);
    }

    public String getCancelAppointmentLabel() {
        return StringUtil.getLabelForView(cancelAppointmentLabel);
    }

    public String getAcceptLabel() {
        return StringUtil.getLabelForView(acceptLabel);
    }

    public String getRejectLabel() {
        return StringUtil.getLabelForView(rejectLabel);
    }
}
