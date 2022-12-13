
package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.utils.SystemUtil;
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
    private String id;
    @SerializedName("resource_id")
    @Expose
    private String resource_id;
    @SerializedName("guid")
    @Expose
    private String guid;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("middle_initial")
    @Expose
    private String middleInitial;
    @SerializedName("prefix")
    @Expose
    private String prefix;
    @SerializedName("suffix")
    @Expose
    private String suffix;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("gender_id")
    @Expose
    private Integer genderId;
    @SerializedName("specialty_name")
    @Expose
    private String specialityName;
    @SerializedName("specialty_taxonomy")
    @Expose
    private String specialityTaxonomy;
    @SerializedName(value = "photo", alternate = "photo_links")
    @Expose
    private String photo;
    @SerializedName("primary_address")
    @Expose
    private PrimaryAddressDto address = new PrimaryAddressDto();
    @SerializedName(value = "phone_number", alternate = "primary_phone")
    @Expose
    private String phone;
    @SerializedName(value = "practice")
    @Expose
    private String practice;

    @SerializedName("npi")
    @Expose
    private String npi;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("speciality")
    @Expose
    private SpecialtyDTO specialty = new SpecialtyDTO();

    public boolean hasError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
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

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * @return the first name plus the last name if fullName is empty
     */
    public String getFullName() {
        if (!SystemUtil.isNotEmptyString(fullName)) {
            fullName = firstName + " " + lastName;
            if (firstName == null || lastName == null) {
                fullName = name;
            }
        }
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public String getSpecialityName() {
        return specialityName;
    }

    public void setSpecialityName(String specialityName) {
        this.specialityName = specialityName;
    }

    public String getSpecialityTaxonomy() {
        return specialityTaxonomy;
    }

    public void setSpecialityTaxonomy(String specialityTaxonomy) {
        this.specialityTaxonomy = specialityTaxonomy;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public PrimaryAddressDto getAddress() {
        return address;
    }

    public void setAddress(PrimaryAddressDto address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public String getNpi() {
        return npi;
    }

    public void setNpi(String npi) {
        this.npi = npi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpecialtyDTO getSpecialty() {
        return specialty;
    }

    public void setSpecialty(SpecialtyDTO specialty) {
        this.specialty = specialty;
    }


}
