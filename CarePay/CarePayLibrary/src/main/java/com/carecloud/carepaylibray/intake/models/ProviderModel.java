
package com.carecloud.carepaylibray.intake.models;


import com.carecloud.carepaylibray.appointments.models.AppointmentResourcesItemDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ProviderModel extends AppointmentResourcesItemDTO {

    @SerializedName("id")
    @Expose
    private String id;
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
    private Specialty specialty = new Specialty();

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The npi
     */
    public String getNpi() {
        return npi;
    }

    /**
     * @param npi The npi
     */
    public void setNpi(String npi) {
        this.npi = npi;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber The phone_number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return The specialty
     */
    public Specialty getSpecialty() {
        return specialty;
    }

    /**
     * @param specialty The specialty
     */
    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }

}
