package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 11/11/2016.
 */

public class PatientHomeScreenTransitionsDTO {

    @SerializedName("practice_mode") @Expose
    private TransitionDTO patientMode;

    @SerializedName("patient_checkin")  @Expose
    private TransitionDTO patientCheckin;

    @SerializedName("patient_checkout") @Expose
    private TransitionDTO patientCheckout;

    @SerializedName("patient_payments") @Expose
    private TransitionDTO patientPayments;

    @SerializedName("shop") @Expose
    private TransitionDTO shop;

    @SerializedName("office_news") @Expose
    private TransitionDTO officeNews;

    public TransitionDTO getPatientMode() {
        return patientMode;
    }

    public void setPatientMode(TransitionDTO patientMode) {
        this.patientMode = patientMode;
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
}
