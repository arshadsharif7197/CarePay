
package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model for appointment providers.
 */
public class ProviderDTO {
    @Expose(serialize = false)
    private boolean error = false;

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("npi")
    @Expose
    private String npi;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("speciality")
    @Expose
    private SpecialtyDTO specialty = new SpecialtyDTO();
    @SerializedName("phone_number")
    @Expose
    private String phone;
    @SerializedName("photo")
    @Expose
    private String photo;

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
    public String getNpi() {
        return npi;
    }

    /**
     * 
     * @param npi
     *     The npi
     */
    public void setNpi(String npi) {
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
    public SpecialtyDTO getSpecialty() {
        return specialty;
    }

    /**
     *
     * @param specialty
     *     The Specialty
     */
    public void setSpecialty(SpecialtyDTO specialty) {
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

    /**
     * @return The photo
     */
    public String getPhoto() {
        return photo;
    }

    /**
     * @param photo The photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean hasError() {
        return error;
    }

    public void setError(boolean hasError) {
        this.error = hasError;
    }


}
