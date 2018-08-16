package com.carecloud.carepaylibray.third_party.models;

import com.google.gson.annotations.SerializedName;

public class ThirdPartyTask {

    @SerializedName("host")
    private String host;

    @SerializedName("return_url")
    private String returnUrl;

    @SerializedName("practice_mgmt")
    private String practiceMgmt;

    @SerializedName("practice_id")
    private String practiceId;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("appointment_id")
    private String appointmentId;

    @SerializedName("location_id")
    private String locationId;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("access_url")
    private String accessUrl;

    @SerializedName("handles_next")
    private boolean handlesNext = true;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getPracticeMgmt() {
        return practiceMgmt;
    }

    public void setPracticeMgmt(String practiceMgmt) {
        this.practiceMgmt = practiceMgmt;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public boolean handlesNext() {
        return handlesNext;
    }

    public void setHandlesNext(boolean handlesNext) {
        this.handlesNext = handlesNext;
    }
}
