
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model for appointment providers.
 */
public class AppointmentProvidersDTO {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("npi")
    @Expose
    private Object npi;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("specialty")
    @Expose
    private String specialty;
    @SerializedName("phone")
    @Expose
    private String phone;

    /**
     * 
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The npi
     */
    public Object getNpi() {
        return npi;
    }

    /**
     * 
     * @param npi
     *     The npi
     */
    public void setNpi(Object npi) {
        this.npi = npi;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The specialty
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     *
     * @param specialty
     *     The Specialty
     */
    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /**
     *
     * @return
     *     The phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     *
     * @param phone
     *     The phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

}
