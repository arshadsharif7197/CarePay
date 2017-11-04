package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/16/17
 */

public class DemographicsPersonalSection {

    @SerializedName("properties")
    @Expose
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public class Properties {

        @SerializedName("first_name")
        @Expose
        private DemographicsField firstName = new DemographicsField();

        @SerializedName("last_name")
        @Expose
        private DemographicsField lastName = new DemographicsField();

        @SerializedName("middle_name")
        @Expose
        private DemographicsField middleName = new DemographicsField();

        @SerializedName("date_of_birth")
        @Expose
        private DemographicsField dateOfBirth = new DemographicsField();

        @SerializedName("gender")
        @Expose
        private DemographicsField gender = new DemographicsField();

        @SerializedName("primary_race")
        @Expose
        private DemographicsField primaryRace = new DemographicsField();

        @SerializedName("secondary_race")
        @Expose
        private DemographicsField secondaryRace = new DemographicsField();

        @SerializedName("ethnicity")
        @Expose
        private DemographicsField ethnicity = new DemographicsField();

        @SerializedName("preferred_language")
        @Expose
        private DemographicsField preferredLanguage = new DemographicsField();

        @SerializedName("profile_photo")
        @Expose
        private DemographicsField profilePhoto = new DemographicsField();

        @SerializedName("preferred_name")
        @Expose
        private DemographicsField preferredName = new DemographicsField();

        @SerializedName("email_address")
        @Expose
        private DemographicsField emailAddress = new DemographicsField();

        @SerializedName("ssn")
        @Expose
        private DemographicsField socialSecurityNumber = new DemographicsField();

        @SerializedName("drivers_license_number")
        @Expose
        private DemographicsField driversLicenseNumber = new DemographicsField();

        @SerializedName("drivers_license_state")
        @Expose
        private DemographicsField driversLicenseState = new DemographicsField();

        @SerializedName("secondary_phone_number")
        @Expose
        private DemographicsField secondaryPhoneNumber = new DemographicsField();

        @SerializedName("secondary_phone_type")
        @Expose
        private DemographicsField secondaryPhoneNumberType = new DemographicsField();

        @SerializedName("preferred_contact")
        @Expose
        private DemographicsField preferredContact = new DemographicsField();

        @SerializedName("marital_status")
        @Expose
        private DemographicsField maritalStatus = new DemographicsField();

        @SerializedName("referral_source")
        @Expose
        private DemographicsField referralSource = new DemographicsField();


        public DemographicsField getFirstName() {
            return firstName;
        }

        public void setFirstName(DemographicsField firstName) {
            this.firstName = firstName;
        }

        public DemographicsField getLastName() {
            return lastName;
        }

        public void setLastName(DemographicsField lastName) {
            this.lastName = lastName;
        }

        public DemographicsField getMiddleName() {
            return middleName;
        }

        public void setMiddleName(DemographicsField middleName) {
            this.middleName = middleName;
        }

        public DemographicsField getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(DemographicsField dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public DemographicsField getGender() {
            return gender;
        }

        public void setGender(DemographicsField gender) {
            this.gender = gender;
        }

        public DemographicsField getPrimaryRace() {
            return primaryRace;
        }

        public void setPrimaryRace(DemographicsField primaryRace) {
            this.primaryRace = primaryRace;
        }

        public DemographicsField getSecondaryRace() {
            return secondaryRace;
        }

        public void setSecondaryRace(DemographicsField secondaryRace) {
            this.secondaryRace = secondaryRace;
        }

        public DemographicsField getEthnicity() {
            return ethnicity;
        }

        public void setEthnicity(DemographicsField ethnicity) {
            this.ethnicity = ethnicity;
        }

        public DemographicsField getPreferredLanguage() {
            return preferredLanguage;
        }

        public void setPreferredLanguage(DemographicsField preferredLanguage) {
            this.preferredLanguage = preferredLanguage;
        }

        public DemographicsField getProfilePhoto() {
            return profilePhoto;
        }

        public void setProfilePhoto(DemographicsField profilePhoto) {
            this.profilePhoto = profilePhoto;
        }

        public DemographicsField getPreferredName() {
            return preferredName;
        }

        public void setPreferredName(DemographicsField preferredName) {
            this.preferredName = preferredName;
        }

        public DemographicsField getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(DemographicsField emailAddress) {
            this.emailAddress = emailAddress;
        }

        public DemographicsField getSocialSecurityNumber() {
            return socialSecurityNumber;
        }

        public void setSocialSecurityNumber(DemographicsField socialSecurityNumber) {
            this.socialSecurityNumber = socialSecurityNumber;
        }

        public DemographicsField getDriversLicenseNumber() {
            return driversLicenseNumber;
        }

        public void setDriversLicenseNumber(DemographicsField driversLicenseNumber) {
            this.driversLicenseNumber = driversLicenseNumber;
        }

        public DemographicsField getDriversLicenseState() {
            return driversLicenseState;
        }

        public void setDriversLicenseState(DemographicsField driversLicenseState) {
            this.driversLicenseState = driversLicenseState;
        }

        public DemographicsField getSecondaryPhoneNumber() {
            return secondaryPhoneNumber;
        }

        public void setSecondaryPhoneNumber(DemographicsField secondaryPhoneNumber) {
            this.secondaryPhoneNumber = secondaryPhoneNumber;
        }

        public DemographicsField getSecondaryPhoneNumberType() {
            return secondaryPhoneNumberType;
        }

        public void setSecondaryPhoneNumberType(DemographicsField secondaryPhoneNumberType) {
            this.secondaryPhoneNumberType = secondaryPhoneNumberType;
        }

        public DemographicsField getPreferredContact() {
            return preferredContact;
        }

        public void setPreferredContact(DemographicsField preferredContact) {
            this.preferredContact = preferredContact;
        }

        public DemographicsField getMaritalStatus() {
            return maritalStatus;
        }

        public void setMaritalStatus(DemographicsField maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public DemographicsField getReferralSource() {
            return referralSource;
        }

        public void setReferralSource(DemographicsField referralSource) {
            this.referralSource = referralSource;
        }
    }
}
