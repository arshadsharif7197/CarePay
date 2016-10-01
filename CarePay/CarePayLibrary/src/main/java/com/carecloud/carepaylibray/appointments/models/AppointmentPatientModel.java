
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppointmentPatientModel {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("chart_number")
    @Expose
    private String chartNumber;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("middle_initial")
    @Expose
    private Object middleInitial;
    @SerializedName("gender_id")
    @Expose
    private String genderId;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("patient_status")
    @Expose
    private String patientStatus;
    @SerializedName("primary_phone_number")
    @Expose
    private Object primaryPhoneNumber;

    @SerializedName("photo")
    @Expose
    private String photo;

    /**
     * 
     * @return
     *     The id
     */
    public String getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The chartNumber
     */
    public String getChartNumber() {
        return chartNumber;
    }

    /**
     * 
     * @param chartNumber
     *     The chart_number
     */
    public void setChartNumber(String chartNumber) {
        this.chartNumber = chartNumber;
    }

    /**
     * 
     * @return
     *     The dateOfBirth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * 
     * @param dateOfBirth
     *     The date_of_birth
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * 
     * @return
     *     The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * 
     * @param firstName
     *     The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * 
     * @return
     *     The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * 
     * @param lastName
     *     The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * 
     * @return
     *     The middleInitial
     */
    public Object getMiddleInitial() {
        return middleInitial;
    }

    /**
     * 
     * @param middleInitial
     *     The middle_initial
     */
    public void setMiddleInitial(Object middleInitial) {
        this.middleInitial = middleInitial;
    }

    /**
     * 
     * @return
     *     The genderId
     */
    public String getGenderId() {
        return genderId;
    }

    /**
     * 
     * @param genderId
     *     The gender_id
     */
    public void setGenderId(String genderId) {
        this.genderId = genderId;
    }

    /**
     * 
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * 
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 
     * @return
     *     The patientStatus
     */
    public String getPatientStatus() {
        return patientStatus;
    }

    /**
     * 
     * @param patientStatus
     *     The patient_status
     */
    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    /**
     * 
     * @return
     *     The primaryPhoneNumber
     */
    public Object getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    /**
     * 
     * @param primaryPhoneNumber
     *     The primary_phone_number
     */
    public void setPrimaryPhoneNumber(Object primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
