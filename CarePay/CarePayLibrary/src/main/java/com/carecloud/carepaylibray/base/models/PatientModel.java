package com.carecloud.carepaylibray.base.models;

import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModel {

    @SerializedName(value = "patient_id", alternate = {"id"})
    @Expose
    private String patientId;

    @SerializedName("first_name")
    @Expose
    private String firstName;

    @SerializedName("middle_name")
    @Expose
    private String middleName;

    @SerializedName("last_name")
    @Expose
    private String lastName;

    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;

    @SerializedName("gender")
    @Expose
    private String gender;

    @SerializedName("ethnicity")
    @Expose
    private String ethnicity;

    @SerializedName("primary_race")
    @Expose
    private String primaryRace;

    @SerializedName("secondary_race")
    @Expose
    private String secondaryRace;

    @SerializedName("profile_photo")
    @Expose
    private String profilePhoto;
    private transient String localUriPhoto;

    @SerializedName("primary_phone_number")
    @Expose
    private String primaryPhoneNumber;

    @SerializedName("preferred_language")
    @Expose
    private String preferredLanguage;

    @SerializedName("preferred_name")
    @Expose
    private String preferredName;

    @SerializedName("email_address")
    @Expose
    private String emailAddress;

    @SerializedName("ssn")
    @Expose
    private String socialSecurityNumber;

    @SerializedName("drivers_license_number")
    @Expose
    private String driversLicenseNumber;

    @SerializedName("drivers_license_state")
    @Expose
    private String driversLicenseState;

    @SerializedName("secondary_phone_number")
    @Expose
    private String secondaryPhoneNumber;

    @SerializedName("secondary_phone_type")
    @Expose
    private String secondaryPhoneNumberType;

    @SerializedName("preferred_contact")
    @Expose
    private String preferredContact;

    @SerializedName("marital_status")
    @Expose
    private String maritalStatus;

    @SerializedName("employment_status")
    @Expose
    private String employmentStatus;

    @SerializedName("employer")
    @Expose
    private String employer;

    @SerializedName("ec_relationship_type")
    @Expose
    private String emergencyContactRelationship;

    @SerializedName("referral_source")
    @Expose
    private String referralSource;

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getPrimaryRace() {
        return primaryRace;
    }

    public void setPrimaryRace(String primaryRace) {
        this.primaryRace = primaryRace;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getSecondaryRace() {
        return secondaryRace;
    }

    public void setSecondaryRace(String secondaryRace) {
        this.secondaryRace = secondaryRace;
    }

    public String getPrimaryPhoneNumber() {
        return primaryPhoneNumber;
    }

    public String getShortName() {
        return StringUtil.getShortName(getFullName());
    }

    public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
        this.primaryPhoneNumber = primaryPhoneNumber;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public void setSocialSecurityNumber(String socialSecurityNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
    }

    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    public String getDriversLicenseState() {
        return driversLicenseState;
    }

    public void setDriversLicenseState(String driversLicenseState) {
        this.driversLicenseState = driversLicenseState;
    }

    public String getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    public void setSecondaryPhoneNumber(String secondaryPhoneNumber) {
        this.secondaryPhoneNumber = secondaryPhoneNumber;
    }

    public String getSecondaryPhoneNumberType() {
        return secondaryPhoneNumberType;
    }

    public void setSecondaryPhoneNumberType(String secondaryPhoneNumberType) {
        this.secondaryPhoneNumberType = secondaryPhoneNumberType;
    }

    public String getPreferredContact() {
        return preferredContact;
    }

    public void setPreferredContact(String preferredContact) {
        this.preferredContact = preferredContact;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public String getEmployer() {
        return employer;
    }

    public void setEmployer(String employer) {
        this.employer = employer;
    }

    public String getEmergencyContactRelationship() {
        return emergencyContactRelationship;
    }

    public void setEmergencyContactRelationship(String emergencyContactRelationship) {
        this.emergencyContactRelationship = emergencyContactRelationship;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }


    /**
     * @return formatted DOB
     */
    public String getFormattedDateOfBirth() {
        if (StringUtil.isNullOrEmpty(dateOfBirth)) {
            return "";
        }

        return DateUtil.getInstance().setDateRaw(dateOfBirth).toStringWithFormatMmSlashDdSlashYyyy();
    }


    /**
     * @return full name
     */
    public String getFullName() {
        String name = "";

        if (null != firstName && !firstName.isEmpty()) {
            name = firstName.trim();
        }

        if (null != middleName && !middleName.isEmpty()) {
            name = (name + " " + middleName).trim();
        }

        if (null != lastName && !lastName.isEmpty()) {
            name = (name + " " + lastName).trim();
        }

        return name;
    }

    public String getLocalUriPhoto() {
        return localUriPhoto;
    }

    public void setLocalUriPhoto(String localUriPhoto) {
        this.localUriPhoto = localUriPhoto;
    }
}
