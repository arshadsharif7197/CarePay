package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenLabelDTO {

    @SerializedName("checkin_button") @Expose
    private String checkinButton;

    @SerializedName("payments_button") @Expose
    private String paymentsButton;

    @SerializedName("appointments_button") @Expose
    private String appointmentsButton;

    @SerializedName("checkout_button") @Expose
    private String checkoutButton;

    @SerializedName("shop_button") @Expose
    private String shopButton;

    @SerializedName("officenews_button")  @Expose
    private String officenewsButton;

    @SerializedName("checkingin_notifications") @Expose
    private String checkinginNotifications;

    @SerializedName("alerts") @Expose
    private String alerts;

    @SerializedName("patient_mode_button") @Expose
    private String patientModeLabel;

    @SerializedName("logout_button") @Expose
    private String logoutLabel;

    /**
     * @return The checkinButton
     */
    public String getCheckinButton() {
        return checkinButton;
    }

    /**
     * @param checkinButton The checkin_button
     */
    public void setCheckinButton(String checkinButton) {
        this.checkinButton = checkinButton;
    }

    /**
     * @return The paymentsButton
     */
    public String getPaymentsButton() {
        return paymentsButton;
    }

    /**
     * @param paymentsButton The payments_button
     */
    public void setPaymentsButton(String paymentsButton) {
        this.paymentsButton = paymentsButton;
    }

    /**
     * @return The appointmentsButton
     */
    public String getAppointmentsButton() {
        return appointmentsButton;
    }

    /**
     * @param appointmentsButton The appointments_button
     */
    public void setAppointmentsButton(String appointmentsButton) {
        this.appointmentsButton = appointmentsButton;
    }

    /**
     * @return The checkoutButton
     */
    public String getCheckoutButton() {
        return checkoutButton;
    }

    /**
     * @param checkoutButton The checkout_button
     */
    public void setCheckoutButton(String checkoutButton) {
        this.checkoutButton = checkoutButton;
    }

    /**
     * @return The shopButton
     */
    public String getShopButton() {
        return shopButton;
    }

    /**
     * @param shopButton The shop_button
     */
    public void setShopButton(String shopButton) {
        this.shopButton = shopButton;
    }

    /**
     * @return The officenewsButton
     */
    public String getOfficenewsButton() {
        return officenewsButton;
    }

    /**
     * @param officenewsButton  The officenews_button
     */
    public void setOfficenewsButton(String officenewsButton) {
        this.officenewsButton = officenewsButton;
    }

    /**
     * @return The checkinginNotifications
     */
    public String getCheckinginNotifications() {
        return checkinginNotifications;
    }

    /**
     * @param checkinginNotifications  The checkingin_notifications
     */
    public void setCheckinginNotifications(String checkinginNotifications) {
        this.checkinginNotifications = checkinginNotifications;
    }

    /**
     * @return The alerts
     */
    public String getAlerts() {
        return alerts;
    }

    /**
     * @param alerts The alerts
     */
    public void setAlerts(String alerts) {
        this.alerts = alerts;
    }

    public String getPatientModeLabel() {
        return patientModeLabel;
    }

    public void setPatientModeLabel(String patientModeLabel) {
        this.patientModeLabel = patientModeLabel;
    }

    public String getLogoutLabel() {
        return logoutLabel;
    }

    public void setLogoutLabel(String logoutLabel) {
        this.logoutLabel = logoutLabel;
    }
}
