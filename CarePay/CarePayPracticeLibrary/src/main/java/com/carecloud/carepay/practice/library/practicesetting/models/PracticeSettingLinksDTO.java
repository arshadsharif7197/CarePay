package com.carecloud.carepay.practice.library.practicesetting.models;

/**
 * Created by prem_mourya on 10/24/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PracticeSettingLinksDTO {

    @SerializedName("self")
    @Expose
    private PracticeSettingMethodDTO self = new PracticeSettingMethodDTO();
    @SerializedName("language")
    @Expose
    private PracticeSettingMethodDTO language = new PracticeSettingMethodDTO();
    @SerializedName("authentication")
    @Expose
    private PracticeSettingMethodDTO authentication = new PracticeSettingMethodDTO();
    @SerializedName("consent_forms")
    @Expose
    private PracticeSettingMethodDTO consentForms = new PracticeSettingMethodDTO();
    @SerializedName("intake_forms")
    @Expose
    private PracticeSettingMethodDTO intakeForms = new PracticeSettingMethodDTO();
    @SerializedName("marketing")
    @Expose
    private PracticeSettingMethodDTO marketing = new PracticeSettingMethodDTO();
    @SerializedName("payment")
    @Expose
    private PracticeSettingMethodDTO payment = new PracticeSettingMethodDTO();
    @SerializedName("queue")
    @Expose
    private PracticeSettingMethodDTO queue = new PracticeSettingMethodDTO();
    @SerializedName("inventory")
    @Expose
    private PracticeSettingMethodDTO inventory = new PracticeSettingMethodDTO();
    @SerializedName("check_out")
    @Expose
    private PracticeSettingMethodDTO checkOut = new PracticeSettingMethodDTO();
    @SerializedName("other")
    @Expose
    private PracticeSettingMethodDTO other = new PracticeSettingMethodDTO();

    /**
     *
     * @return
     *     The self
     */
    public PracticeSettingMethodDTO getSelf() {
        return self;
    }

    /**
     *
     * @param self
     *     The self
     */
    public void setSelf(PracticeSettingMethodDTO self) {
        this.self = self;
    }

    /**
     *
     * @return
     *     The language
     */
    public PracticeSettingMethodDTO getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     *     The language
     */
    public void setLanguage(PracticeSettingMethodDTO language) {
        this.language = language;
    }

    /**
     *
     * @return
     *     The authentication
     */
    public PracticeSettingMethodDTO getAuthentication() {
        return authentication;
    }

    /**
     *
     * @param authentication
     *     The authentication
     */
    public void setAuthentication(PracticeSettingMethodDTO authentication) {
        this.authentication = authentication;
    }

    /**
     *
     * @return
     *     The consentForms
     */
    public PracticeSettingMethodDTO getConsentForms() {
        return consentForms;
    }

    /**
     *
     * @param consentForms
     *     The consent_forms
     */
    public void setConsentForms(PracticeSettingMethodDTO consentForms) {
        this.consentForms = consentForms;
    }

    /**
     *
     * @return
     *     The intakeForms
     */
    public PracticeSettingMethodDTO getIntakeForms() {
        return intakeForms;
    }

    /**
     *
     * @param intakeForms
     *     The intake_forms
     */
    public void setIntakeForms(PracticeSettingMethodDTO intakeForms) {
        this.intakeForms = intakeForms;
    }

    /**
     *
     * @return
     *     The marketing
     */
    public PracticeSettingMethodDTO getMarketing() {
        return marketing;
    }

    /**
     *
     * @param marketing
     *     The marketing
     */
    public void setMarketing(PracticeSettingMethodDTO marketing) {
        this.marketing = marketing;
    }

    /**
     *
     * @return
     *     The payment
     */
    public PracticeSettingMethodDTO getPayment() {
        return payment;
    }

    /**
     *
     * @param payment
     *     The payment
     */
    public void setPayment(PracticeSettingMethodDTO payment) {
        this.payment = payment;
    }

    /**
     *
     * @return
     *     The queue
     */
    public PracticeSettingMethodDTO getQueue() {
        return queue;
    }

    /**
     *
     * @param queue
     *     The queue
     */
    public void setQueue(PracticeSettingMethodDTO queue) {
        this.queue = queue;
    }

    /**
     *
     * @return
     *     The inventory
     */
    public PracticeSettingMethodDTO getInventory() {
        return inventory;
    }

    /**
     *
     * @param inventory
     *     The inventory
     */
    public void setInventory(PracticeSettingMethodDTO inventory) {
        this.inventory = inventory;
    }

    /**
     *
     * @return
     *     The checkOut
     */
    public PracticeSettingMethodDTO getCheckOut() {
        return checkOut;
    }

    /**
     *
     * @param checkOut
     *     The check_out
     */
    public void setCheckOut(PracticeSettingMethodDTO checkOut) {
        this.checkOut = checkOut;
    }

    /**
     *
     * @return
     *     The other
     */
    public PracticeSettingMethodDTO getOther() {
        return other;
    }

    /**
     *
     * @param other
     *     The other
     */
    public void setOther(PracticeSettingMethodDTO other) {
        this.other = other;
    }

}

