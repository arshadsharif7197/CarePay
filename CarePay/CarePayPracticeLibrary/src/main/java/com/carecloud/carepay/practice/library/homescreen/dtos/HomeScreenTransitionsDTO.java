package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenTransitionsDTO {

    @SerializedName("patient_mode")
    @Expose
    private TransitionDTO patientMode;
    @SerializedName("logout")
    @Expose
    private TransitionDTO logout;
    @SerializedName("practice_checkin")
    @Expose
    private TransitionDTO practiceCheckin;
    @SerializedName("practice_payments")
    @Expose
    private TransitionDTO practicePayments;
    @SerializedName("practice_checkout")
    @Expose
    private TransitionDTO practiceCheckout;
    @SerializedName("shop")
    @Expose
    private TransitionDTO shop;
    @SerializedName("office_news")
    @Expose
    private TransitionDTO officeNews;

    /**
     *
     * @return
     * The patientMode
     */
    public TransitionDTO getPatientMode() {
        return patientMode;
    }

    /**
     *
     * @param patientMode
     * The patient_mode
     */
    public void setPatientMode(TransitionDTO patientMode) {
        this.patientMode = patientMode;
    }

    /**
     *
     * @return
     * The logout
     */
    public TransitionDTO getLogout() {
        return logout;
    }

    /**
     *
     * @param logout
     * The logout
     */
    public void setLogout(TransitionDTO logout) {
        this.logout = logout;
    }

    /**
     *
     * @return
     * The practiceCheckin
     */
    public TransitionDTO getPracticeCheckin() {
        return practiceCheckin;
    }

    /**
     *
     * @param practiceCheckin
     * The practice_checkin
     */
    public void setPracticeCheckin(TransitionDTO practiceCheckin) {
        this.practiceCheckin = practiceCheckin;
    }

    /**
     *
     * @return
     * The practicePayments
     */
    public TransitionDTO getPracticePayments() {
        return practicePayments;
    }

    /**
     *
     * @param practicePayments
     * The practice_payments
     */
    public void setPracticePayments(TransitionDTO practicePayments) {
        this.practicePayments = practicePayments;
    }

    /**
     *
     * @return
     * The practiceCheckout
     */
    public TransitionDTO getPracticeCheckout() {
        return practiceCheckout;
    }

    /**
     *
     * @param practiceCheckout
     * The practice_checkout
     */
    public void setPracticeCheckout(TransitionDTO practiceCheckout) {
        this.practiceCheckout = practiceCheckout;
    }

    /**
     *
     * @return
     * The shop
     */
    public TransitionDTO getShop() {
        return shop;
    }

    /**
     *
     * @param shop
     * The shop
     */
    public void setShop(TransitionDTO shop) {
        this.shop = shop;
    }

    /**
     *
     * @return
     * The officeNews
     */
    public TransitionDTO getOfficeNews() {
        return officeNews;
    }

    /**
     *
     * @param officeNews
     * The office_news
     */
    public void setOfficeNews(TransitionDTO officeNews) {
        this.officeNews = officeNews;
    }
}
