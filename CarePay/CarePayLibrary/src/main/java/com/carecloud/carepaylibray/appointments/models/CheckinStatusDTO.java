package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 10/12/17
 */

public class CheckinStatusDTO {

    @SerializedName("intake_forms")
    private String intakeStatus;

    @SerializedName("demographics_verify")
    private String demographicsStatus;

    @SerializedName("consent_forms")
    private String consentFormsStatus;

    @SerializedName("medications_allergies")
    private String medicationsStatus;

    @SerializedName("checkout_forms")
    private String checkoutFormsStatus;

    @SerializedName("payment_checkout")
    private String checkoutPaymentStatus;

    @SerializedName("appointments_checkout")
    private String checkoutAppointmentStatus;

    public String getIntakeStatus() {
        return intakeStatus;
    }

    public void setIntakeStatus(String intakeStatus) {
        this.intakeStatus = intakeStatus;
    }

    public String getDemographicsStatus() {
        return demographicsStatus;
    }

    public void setDemographicsStatus(String demographicsStatus) {
        this.demographicsStatus = demographicsStatus;
    }

    public String getConsentFormsStatus() {
        return consentFormsStatus;
    }

    public void setConsentFormsStatus(String consentFormsStatus) {
        this.consentFormsStatus = consentFormsStatus;
    }

    public String getMedicationsStatus() {
        return medicationsStatus;
    }

    public void setMedicationsStatus(String medicationsStatus) {
        this.medicationsStatus = medicationsStatus;
    }

    public String getCheckoutFormsStatus() {
        return checkoutFormsStatus;
    }

    public void setCheckoutFormsStatus(String checkoutFormsStatus) {
        this.checkoutFormsStatus = checkoutFormsStatus;
    }

    public String getCheckoutPaymentStatus() {
        return checkoutPaymentStatus;
    }

    public void setCheckoutPaymentStatus(String checkoutPaymentStatus) {
        this.checkoutPaymentStatus = checkoutPaymentStatus;
    }

    public String getCheckoutAppointmentStatus() {
        return checkoutAppointmentStatus;
    }

    public void setCheckoutAppointmentStatus(String checkoutAppointmentStatus) {
        this.checkoutAppointmentStatus = checkoutAppointmentStatus;
    }
}
