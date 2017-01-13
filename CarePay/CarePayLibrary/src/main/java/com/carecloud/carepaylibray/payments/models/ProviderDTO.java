package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class ProviderDTO {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("npi")
    @Expose
    private String npi;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;
    @SerializedName("specialty")
    @Expose
    private SpecialtyDTO specialty;

    /**
     *
     * @return
     * The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The npi
     */
    public String getNpi() {
        return npi;
    }

    /**
     *
     * @param npi
     * The npi
     */
    public void setNpi(String npi) {
        this.npi = npi;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     *
     * @param phoneNumber
     * The phone_number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     *
     * @return
     * The specialty
     */
    public SpecialtyDTO getSpecialty() {
        return specialty;
    }

    /**
     *
     * @param specialty
     * The specialty
     */
    public void setSpecialty(SpecialtyDTO specialty) {
        this.specialty = specialty;
    }
}
