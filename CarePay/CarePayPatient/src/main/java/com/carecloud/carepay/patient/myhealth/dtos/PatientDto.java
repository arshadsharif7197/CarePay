package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.appointments.models.PrimaryAddressDto;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 24/07/17.
 */

public class PatientDto {

    @SerializedName("id")
    private Integer id;
    @SerializedName("guid")
    private String guid;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("middle_initial")
    private String middleInitial;
    @SerializedName("suffix")
    private String sufix;
    @SerializedName("prefix")
    private String prefix;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("email")
    private String email;
    @SerializedName("date_of_birth")
    private String dateOfBirth;
    @SerializedName("gender_id")
    private Integer gender;
    @SerializedName("primary_address")
    private PrimaryAddressDto primaryAddress;
    @SerializedName("profile_photo")
    private String profilePhoto;
    @SerializedName("practice")
    private String practice;
    @SerializedName("business_entity")
    private BusinessEntity businessEntity = new BusinessEntity();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getSufix() {
        return sufix;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFullName() {
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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public PrimaryAddressDto getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(PrimaryAddressDto primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }
}
