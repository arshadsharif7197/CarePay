package com.carecloud.carepaylibray.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 3/4/19.
 */
public class Permissions {

    @SerializedName("checkin_and_checkout")
    @Expose
    private Permission checkInAndCheckOut;
    @SerializedName("edit_account_settings")
    @Expose
    private Permission editAccountSettings;
    @SerializedName("make_payments")
    @Expose
    private Permission makePayments;
    @SerializedName("message_providers")
    @Expose
    private Permission messageProviders;
    @SerializedName("review_forms")
    @Expose
    private Permission reviewForms;
    @SerializedName("schedule_appointments")
    @Expose
    private Permission scheduleAppointments;
    @SerializedName("view_account_settings")
    @Expose
    private Permission viewAccoutSettings;
    @SerializedName("view_allergies")
    @Expose
    private Permission viewAllergies;
    @SerializedName("view_all_my_health")
    @Expose
    private Permission viewAllMyHealth;
    @SerializedName("view_and_create_visit_summaries")
    @Expose
    private Permission viewAndCreateVisitSummaries;
    @SerializedName("view_and_submit_surveys")
    @Expose
    private Permission viewAndSubmitSurveys;
    @SerializedName("view_appointments")
    @Expose
    private Permission viewAppointments;
    @SerializedName("view_balance_and_historical_payments")
    @Expose
    private Permission viewBalanceAndHistoricalPayments;
    @SerializedName("view_balance_details")
    @Expose
    private Permission viewBalanceDetails;
    @SerializedName("view_care_team")
    @Expose
    private Permission viewCareTeam;
    @SerializedName("view_conditions")
    @Expose
    private Permission viewConditions;
    @SerializedName("view_forms")
    @Expose
    private Permission viewForms;
    @SerializedName("view_lab_results")
    @Expose
    private Permission viewLabResults;
    @SerializedName("view_medications")
    @Expose
    private Permission viewMedications;
    @SerializedName("view_messages")
    @Expose
    private Permission viewMessages;
    @SerializedName("view_notifications")
    @Expose
    private Permission viewNotifications;
    @SerializedName("view_patient_statements")
    @Expose
    private Permission viewPatientStatements;

    public Permission getCheckInAndCheckOut() {
        return checkInAndCheckOut;
    }

    public void setCheckInAndCheckOut(Permission checkInAndCheckOut) {
        this.checkInAndCheckOut = checkInAndCheckOut;
    }

    public Permission getEditAccountSettings() {
        return editAccountSettings;
    }

    public void setEditAccountSettings(Permission editAccountSettings) {
        this.editAccountSettings = editAccountSettings;
    }

    public Permission getMakePayments() {
        return makePayments;
    }

    public void setMakePayments(Permission makePayments) {
        this.makePayments = makePayments;
    }

    public Permission getMessageProviders() {
        return messageProviders;
    }

    public void setMessageProviders(Permission messageProviders) {
        this.messageProviders = messageProviders;
    }

    public Permission getReviewForms() {
        return reviewForms;
    }

    public void setReviewForms(Permission reviewForms) {
        this.reviewForms = reviewForms;
    }

    public Permission getScheduleAppointments() {
        return scheduleAppointments;
    }

    public void setScheduleAppointments(Permission scheduleAppointments) {
        this.scheduleAppointments = scheduleAppointments;
    }

    public Permission getViewAccoutSettings() {
        return viewAccoutSettings;
    }

    public void setViewAccoutSettings(Permission viewAccoutSettings) {
        this.viewAccoutSettings = viewAccoutSettings;
    }

    public Permission getViewAllergies() {
        return viewAllergies;
    }

    public void setViewAllergies(Permission viewAllergies) {
        this.viewAllergies = viewAllergies;
    }

    public Permission getViewAllMyHealth() {
        return viewAllMyHealth;
    }

    public void setViewAllMyHealth(Permission viewAllMyHealth) {
        this.viewAllMyHealth = viewAllMyHealth;
    }

    public Permission getViewAndCreateVisitSummaries() {
        return viewAndCreateVisitSummaries;
    }

    public void setViewAndCreateVisitSummaries(Permission viewAndCreateVisitSummaries) {
        this.viewAndCreateVisitSummaries = viewAndCreateVisitSummaries;
    }

    public Permission getViewAndSubmitSurveys() {
        return viewAndSubmitSurveys;
    }

    public void setViewAndSubmitSurveys(Permission viewAndSubmitSurveys) {
        this.viewAndSubmitSurveys = viewAndSubmitSurveys;
    }

    public Permission getViewAppointments() {
        return viewAppointments;
    }

    public void setViewAppointments(Permission viewAppointments) {
        this.viewAppointments = viewAppointments;
    }

    public Permission getViewBalanceAndHistoricalPayments() {
        return viewBalanceAndHistoricalPayments;
    }

    public void setViewBalanceAndHistoricalPayments(Permission viewBalanceAndHistoricalPayments) {
        this.viewBalanceAndHistoricalPayments = viewBalanceAndHistoricalPayments;
    }

    public Permission getViewBalanceDetails() {
        return viewBalanceDetails;
    }

    public void setViewBalanceDetails(Permission viewBalanceDetails) {
        this.viewBalanceDetails = viewBalanceDetails;
    }

    public Permission getViewCareTeam() {
        return viewCareTeam;
    }

    public void setViewCareTeam(Permission viewCareTeam) {
        this.viewCareTeam = viewCareTeam;
    }

    public Permission getViewConditions() {
        return viewConditions;
    }

    public void setViewConditions(Permission viewConditions) {
        this.viewConditions = viewConditions;
    }

    public Permission getViewForms() {
        return viewForms;
    }

    public void setViewForms(Permission viewForms) {
        this.viewForms = viewForms;
    }

    public Permission getViewLabResults() {
        return viewLabResults;
    }

    public void setViewLabResults(Permission viewLabResults) {
        this.viewLabResults = viewLabResults;
    }

    public Permission getViewMedications() {
        return viewMedications;
    }

    public void setViewMedications(Permission viewMedications) {
        this.viewMedications = viewMedications;
    }

    public Permission getViewMessages() {
        return viewMessages;
    }

    public void setViewMessages(Permission viewMessages) {
        this.viewMessages = viewMessages;
    }

    public Permission getViewNotifications() {
        return viewNotifications;
    }

    public void setViewNotifications(Permission viewNotifications) {
        this.viewNotifications = viewNotifications;
    }

    public Permission getViewPatientStatements() {
        return viewPatientStatements;
    }

    public void setViewPatientStatements(Permission viewPatientStatements) {
        this.viewPatientStatements = viewPatientStatements;
    }
}
