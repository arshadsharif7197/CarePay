
package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PatientModeHomeLabelsDTO {

    @SerializedName("checkin_button")
    @Expose
    private String checkinButton;
    @SerializedName("payments_button")
    @Expose
    private String paymentsButton;
    @SerializedName("appointments_button")
    @Expose
    private String appointmentsButton;
    @SerializedName("checkout_button")
    @Expose
    private String checkoutButton;
    @SerializedName("shop_button")
    @Expose
    private String shopButton;
    @SerializedName("officenews_button")
    @Expose
    private String officenewsButton;

    /**
     * 
     * @return
     *     The checkinButton
     */
    public String getCheckinButton() {
        return  StringUtil.getLabelForView(checkinButton);
    }

    /**
     * 
     * @param checkinButton
     *     The checkin_button
     */
    public void setCheckinButton(String checkinButton) {
        this.checkinButton = checkinButton;
    }

    /**
     * 
     * @return
     *     The paymentsButton
     */
    public String getPaymentsButton() {
        return  StringUtil.getLabelForView(paymentsButton);
    }

    /**
     * 
     * @param paymentsButton
     *     The payments_button
     */
    public void setPaymentsButton(String paymentsButton) {
        this.paymentsButton = paymentsButton;
    }

    /**
     * 
     * @return
     *     The appointmentsButton
     */
    public String getAppointmentsButton() {
        return StringUtil.getLabelForView(appointmentsButton);
    }

    /**
     * 
     * @param appointmentsButton
     *     The appointments_button
     */
    public void setAppointmentsButton(String appointmentsButton) {
        this.appointmentsButton = appointmentsButton;
    }

    /**
     * 
     * @return
     *     The checkoutButton
     */
    public String getCheckoutButton() {
        return StringUtil.getLabelForView(checkoutButton);
    }

    /**
     * 
     * @param checkoutButton
     *     The checkout_button
     */
    public void setCheckoutButton(String checkoutButton) {
        this.checkoutButton = checkoutButton;
    }

    /**
     * 
     * @return
     *     The shopButton
     */
    public String getShopButton() {
        return  StringUtil.getLabelForView(shopButton);
    }

    /**
     * 
     * @param shopButton
     *     The shop_button
     */
    public void setShopButton(String shopButton) {
        this.shopButton = shopButton;
    }

    /**
     * 
     * @return
     *     The officenewsButton
     */
    public String getOfficenewsButton() {
        return  StringUtil.getLabelForView(officenewsButton);
    }

    /**
     * 
     * @param officenewsButton
     *     The officenews_button
     */
    public void setOfficenewsButton(String officenewsButton) {
        this.officenewsButton = officenewsButton;
    }

}
