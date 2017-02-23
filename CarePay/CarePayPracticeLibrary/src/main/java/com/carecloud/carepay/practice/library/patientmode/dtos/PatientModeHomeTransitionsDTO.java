
package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PatientModeHomeTransitionsDTO {

    @SerializedName("practice_mode")
    @Expose
    private TransitionDTO practiceMode = new TransitionDTO();
    @SerializedName("patient_checkin")
    @Expose
    private TransitionDTO patientCheckin = new TransitionDTO();
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

    /**
     * 
     * @return
     *     The practiceMode
     */
    public TransitionDTO getPracticeMode() {
        return practiceMode;
    }

    /**
     * 
     * @param practiceMode
     *     The practice_mode
     */
    public void setPracticeMode(TransitionDTO practiceMode) {
        this.practiceMode = practiceMode;
    }

    /**
     * 
     * @return
     *     The patientCheckin
     */
    public TransitionDTO getPatientCheckin() {
        return patientCheckin;
    }

    /**
     * 
     * @param patientCheckin
     *     The patient_checkin
     */
    public void setPatientCheckin(TransitionDTO patientCheckin) {
        this.patientCheckin = patientCheckin;
    }

    /**
     * 
     * @return
     *     The patientPayments
     */
    public TransitionDTO getPatientPayments() {
        return patientPayments;
    }

    /**
     * 
     * @param patientPayments
     *     The patient_payments
     */
    public void setPatientPayments(TransitionDTO patientPayments) {
        this.patientPayments = patientPayments;
    }

    /**
     * 
     * @return
     *     The patientCheckout
     */
    public TransitionDTO getPatientCheckout() {
        return patientCheckout;
    }

    /**
     * 
     * @param patientCheckout
     *     The patient_checkout
     */
    public void setPatientCheckout(TransitionDTO patientCheckout) {
        this.patientCheckout = patientCheckout;
    }

    /**
     * 
     * @return
     *     The shop
     */
    public TransitionDTO getShop() {
        return shop;
    }

    /**
     * 
     * @param shop
     *     The shop
     */
    public void setShop(TransitionDTO shop) {
        this.shop = shop;
    }

    /**
     * 
     * @return
     *     The officeNews
     */
    public TransitionDTO getOfficeNews() {
        return officeNews;
    }

    /**
     * 
     * @param officeNews
     *     The office_news
     */
    public void setOfficeNews(TransitionDTO officeNews) {
        this.officeNews = officeNews;
    }

}
