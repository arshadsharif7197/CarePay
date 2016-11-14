package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepaylibray.utils.StringUtil;
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
    @SerializedName("practice_mode_switch_pin_header")
    @Expose
    private String practiceModeSwitchPinHeader;
    @SerializedName("practice_mode_switch_pin_enter_unlock")
    @Expose
    private String practiceModeSwitchPinEnterUnlock;
    @SerializedName("practice_mode_switch_pin_cancel")
    @Expose
    private String practiceModeSwitchPinCancel;
    @SerializedName("practice_mode_switch_pin_zero")
    @Expose
    private String practiceModeSwitchPinZero;
    @SerializedName("practice_mode_switch_pin_one")
    @Expose
    private String practiceModeSwitchPinOne;
    @SerializedName("practice_mode_switch_pin_two")
    @Expose
    private String practiceModeSwitchPinTwo;
    @SerializedName("practice_mode_switch_pin_three")
    @Expose
    private String practiceModeSwitchPinThree;
    @SerializedName("practice_mode_switch_pin_four")
    @Expose
    private String practiceModeSwitchPinFour;
    @SerializedName("practice_mode_switch_pin_five")
    @Expose
    private String practiceModeSwitchPinFive;
    @SerializedName("practice_mode_switch_pin_six")
    @Expose
    private String practiceModeSwitchPinSix;
    @SerializedName("practice_mode_switch_pin_seven")
    @Expose
    private String practiceModeSwitchPinSeven;
    @SerializedName("practice_mode_switch_pin_eight")
    @Expose
    private String practiceModeSwitchPinEight;
    @SerializedName("practice_mode_switch_pin_nine")
    @Expose
    private String practiceModeSwitchPinNine;

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


    public String getPracticeModeSwitchPinHeader() {
        return StringUtil.getLabelForView(practiceModeSwitchPinHeader) ;
    }

    public void setPracticeModeSwitchPinHeader(String practiceModeSwitchPinHeader) {
        this.practiceModeSwitchPinHeader = practiceModeSwitchPinHeader;
    }

    public String getPracticeModeSwitchPinEnterUnlock() {
        return StringUtil.getLabelForView(practiceModeSwitchPinEnterUnlock) ;
    }

    public void setPracticeModeSwitchPinEnterUnlock(String practiceModeSwitchPinEnterUnlock) {
        this.practiceModeSwitchPinEnterUnlock = practiceModeSwitchPinEnterUnlock;
    }

    public String getPracticeModeSwitchPinCancel() {
        return StringUtil.getLabelForView(practiceModeSwitchPinCancel) ;
    }

    public void setPracticeModeSwitchPinCancel(String practiceModeSwitchPinCancel) {
        this.practiceModeSwitchPinCancel = practiceModeSwitchPinCancel;
    }

    public String getPracticeModeSwitchPinZero() {
        return StringUtil.getLabelForView(practiceModeSwitchPinZero) ;
    }

    public void setPracticeModeSwitchPinZero(String practiceModeSwitchPinZero) {
        this.practiceModeSwitchPinZero = practiceModeSwitchPinZero;
    }

    public String getPracticeModeSwitchPinOne() {
        return StringUtil.getLabelForView(practiceModeSwitchPinOne) ;
    }

    public void setPracticeModeSwitchPinOne(String practiceModeSwitchPinOne) {
        this.practiceModeSwitchPinOne = practiceModeSwitchPinOne;
    }

    public String getPracticeModeSwitchPinTwo() {
        return StringUtil.getLabelForView(practiceModeSwitchPinTwo) ;
    }

    public void setPracticeModeSwitchPinTwo(String practiceModeSwitchPinTwo) {
        this.practiceModeSwitchPinTwo = practiceModeSwitchPinTwo;
    }

    public String getPracticeModeSwitchPinThree() {
        return StringUtil.getLabelForView(practiceModeSwitchPinThree) ;
    }

    public void setPracticeModeSwitchPinThree(String practiceModeSwitchPinThree) {
        this.practiceModeSwitchPinThree = practiceModeSwitchPinThree;
    }

    public String getPracticeModeSwitchPinFour() {
        return StringUtil.getLabelForView(practiceModeSwitchPinFour) ;
    }

    public void setPracticeModeSwitchPinFour(String practiceModeSwitchPinFour) {
        this.practiceModeSwitchPinFour = practiceModeSwitchPinFour;
    }

    public String getPracticeModeSwitchPinFive() {
        return StringUtil.getLabelForView(practiceModeSwitchPinFive) ;
    }

    public void setPracticeModeSwitchPinFive(String practiceModeSwitchPinFive) {
        this.practiceModeSwitchPinFive = practiceModeSwitchPinFive;
    }

    public String getPracticeModeSwitchPinSix() {
        return StringUtil.getLabelForView(practiceModeSwitchPinSix) ;
    }

    public void setPracticeModeSwitchPinSix(String practiceModeSwitchPinSix) {
        this.practiceModeSwitchPinSix = practiceModeSwitchPinSix;
    }

    public String getPracticeModeSwitchPinSeven() {
        return StringUtil.getLabelForView(practiceModeSwitchPinSeven) ;
    }

    public void setPracticeModeSwitchPinSeven(String practiceModeSwitchPinSeven) {
        this.practiceModeSwitchPinSeven = practiceModeSwitchPinSeven;
    }

    public String getPracticeModeSwitchPinEight() {
        return StringUtil.getLabelForView(practiceModeSwitchPinEight) ;
    }

    public void setPracticeModeSwitchPinEight(String practiceModeSwitchPinEight) {
        this.practiceModeSwitchPinEight = practiceModeSwitchPinEight;
    }

    public String getPracticeModeSwitchPinNine() {
        return StringUtil.getLabelForView(practiceModeSwitchPinNine) ;
    }

    public void setPracticeModeSwitchPinNine(String practiceModeSwitchPinNine) {
        this.practiceModeSwitchPinNine = practiceModeSwitchPinNine;
    }
}
