package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 11/11/2016.
 */

public class PatientHomeScreenTransitionsDTO {

    @SerializedName("practice_mode")
    @Expose
    private TransitionDTO practiceMode = new TransitionDTO();
    @SerializedName("patient_checkin")
    @Expose
    private TransitionDTO patientCheckin = new TransitionDTO();
    @SerializedName("patient_appointments")
    @Expose
    private TransitionDTO patientAppointments = new TransitionDTO();
    @SerializedName("patient_payments")
    @Expose
    private TransitionDTO patientPayments = new TransitionDTO();
    @SerializedName("patient_checkout")
    @Expose
    private TransitionDTO patientCheckout = new TransitionDTO();
    @SerializedName("shop")
    @Expose
    private TransitionDTO shop = new TransitionDTO();
    @SerializedName("office_news")
    @Expose
    private TransitionDTO officeNews = new TransitionDTO();

    public TransitionDTO getPracticeMode() {
        return practiceMode;
    }

    public void setPracticeMode(TransitionDTO practiceMode) {
        this.practiceMode = practiceMode;
    }

    public TransitionDTO getPatientCheckin() {
        return patientCheckin;
    }

    public void setPatientCheckin(TransitionDTO patientCheckin) {
        this.patientCheckin = patientCheckin;
    }

    public TransitionDTO getPatientCheckout() {
        return patientCheckout;
    }

    public void setPatientCheckout(TransitionDTO patientCheckout) {
        this.patientCheckout = patientCheckout;
    }

    public TransitionDTO getPatientPayments() {
        return patientPayments;
    }

    public void setPatientPayments(TransitionDTO patientPayments) {
        this.patientPayments = patientPayments;
    }

    public TransitionDTO getShop() {
        return shop;
    }

    public void setShop(TransitionDTO shop) {
        this.shop = shop;
    }

    public TransitionDTO getOfficeNews() {
        return officeNews;
    }

    public void setOfficeNews(TransitionDTO officeNews) {
        this.officeNews = officeNews;
    }

    public TransitionDTO getPatientAppointments() {
        return patientAppointments;
    }

    public void setPatientAppointments(TransitionDTO patientAppointments) {
        this.patientAppointments = patientAppointments;
    }
}
