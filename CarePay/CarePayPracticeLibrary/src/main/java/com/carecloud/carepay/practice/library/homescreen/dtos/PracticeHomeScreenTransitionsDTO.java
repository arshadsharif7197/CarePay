package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class PracticeHomeScreenTransitionsDTO {

    @SerializedName("patient_mode")
    @Expose
    private TransitionDTO patientMode = new TransitionDTO();
    @SerializedName("logout")
    @Expose
    private TransitionDTO logout = new TransitionDTO();
    @SerializedName("practice_checkin")
    @Expose
    private TransitionDTO practiceCheckin = new TransitionDTO();
    @SerializedName("practice_payments")
    @Expose
    private TransitionDTO practicePayments = new TransitionDTO();
    @SerializedName("practice_appointments")
    @Expose
    private TransitionDTO practiceAppointments = new TransitionDTO();
    @SerializedName("practice_checkout")
    @Expose
    private TransitionDTO practiceCheckout = new TransitionDTO();
    @SerializedName("shop")
    @Expose
    private TransitionDTO shop = new TransitionDTO();
    @SerializedName("office_news")
    @Expose
    private TransitionDTO officeNews = new TransitionDTO();
    @SerializedName("office_news_post")
    @Expose
    private TransitionDTO officeNewsPost = new TransitionDTO();
    @SerializedName(value = "language_metadata", alternate = "language")
    @Expose
    private TransitionDTO language = new TransitionDTO();
    @SerializedName("appointment_counts")
    @Expose
    private TransitionDTO appointmentCounts = new TransitionDTO();

    /**
     * @return The patientMode
     */
    public TransitionDTO getPatientMode() {
        return patientMode;
    }

    /**
     * @param patientMode The patient_mode
     */
    public void setPatientMode(TransitionDTO patientMode) {
        this.patientMode = patientMode;
    }

    /**
     * @return The logout
     */
    public TransitionDTO getLogout() {
        return logout;
    }

    /**
     * @param logout The logout
     */
    public void setLogout(TransitionDTO logout) {
        this.logout = logout;
    }

    /**
     * @return The practiceCheckin
     */
    public TransitionDTO getPracticeCheckin() {
        return practiceCheckin;
    }

    /**
     * @param practiceCheckin The practice_checkin
     */
    public void setPracticeCheckin(TransitionDTO practiceCheckin) {
        this.practiceCheckin = practiceCheckin;
    }

    /**
     * @return The practicePayments
     */
    public TransitionDTO getPracticePayments() {
        return practicePayments;
    }

    /**
     * @param practicePayments The practice_payments
     */
    public void setPracticePayments(TransitionDTO practicePayments) {
        this.practicePayments = practicePayments;
    }

    /**
     * @return The practiceCheckout
     */
    public TransitionDTO getPracticeCheckout() {
        return practiceCheckout;
    }

    /**
     * @param practiceCheckout The practice_checkout
     */
    public void setPracticeCheckout(TransitionDTO practiceCheckout) {
        this.practiceCheckout = practiceCheckout;
    }

    /**
     * @return The shop
     */
    public TransitionDTO getShop() {
        return shop;
    }

    /**
     * @param shop The shop
     */
    public void setShop(TransitionDTO shop) {
        this.shop = shop;
    }

    /**
     * @return The officeNews
     */
    public TransitionDTO getOfficeNews() {
        return officeNews;
    }

    /**
     * @param officeNews The office_news
     */
    public void setOfficeNews(TransitionDTO officeNews) {
        this.officeNews = officeNews;
    }

    /**
     * @return practiceAppointments
     */
    public TransitionDTO getPracticeAppointments() {
        return practiceAppointments;
    }

    /**
     * @param practiceAppointments practiceAppointments
     */
    public void setPracticeAppointments(TransitionDTO practiceAppointments) {
        this.practiceAppointments = practiceAppointments;
    }

    /**
     * officeNewsPost
     *
     * @return officeNewsPost
     */
    public TransitionDTO getOfficeNewsPost() {
        return officeNewsPost;
    }

    /**
     * officeNewsPost
     *
     * @param officeNewsPost officeNewsPost
     */
    public void setOfficeNewsPost(TransitionDTO officeNewsPost) {
        this.officeNewsPost = officeNewsPost;
    }

    public TransitionDTO getLanguage() {
        return language;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }

    public TransitionDTO getAppointmentCounts() {
        return appointmentCounts;
    }

    public void setAppointmentCounts(TransitionDTO appointmentCounts) {
        this.appointmentCounts = appointmentCounts;
    }
}
