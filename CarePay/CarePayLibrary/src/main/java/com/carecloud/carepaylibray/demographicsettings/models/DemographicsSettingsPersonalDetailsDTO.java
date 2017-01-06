package com.carecloud.carepaylibray.demographicsettings.models;

/**
 * Created by harshal_patil on 1/5/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsPersonalDetailsDTO {

    @SerializedName("first_name")
    @Expose
    private DemographicsSettingsFirstNameDTO firstName;
    @SerializedName("last_name")
    @Expose
    private DemographicsSettingsLastNameDTO lastName;
    @SerializedName("middle_name")
    @Expose
    private DemographicsSettingsMiddleNameDTO middleName;
    @SerializedName("date_of_birth")
    @Expose
    private DemographicsSettingsDateOfBirthDTO dateOfBirth;
    @SerializedName("gender")
    @Expose
    private DemographicsSettingsGenderDTO gender;
    @SerializedName("primary_race")
    @Expose
    private DemographicsSettingsPrimaryRaceDTO primaryRace;
    @SerializedName("secondary_race")
    @Expose
    private DemographicsSettingsPrimaryRaceDTO secondaryRace;
    @SerializedName("ethnicity")
    @Expose
    private DemographicsSettingsEthnicityDTO ethnicity;
    @SerializedName("preferred_language")
    @Expose
    private DemographicsSettingsPreferredLanguageDTO preferredLanguage;
    @SerializedName("profile_photo")
    @Expose
    private DemographicsSettingsProfilePhotoDTO profilePhoto;

    public DemographicsSettingsFirstNameDTO getFirstName() {
        return firstName;
    }

    public void setFirstName(DemographicsSettingsFirstNameDTO firstName) {
        this.firstName = firstName;
    }

    public DemographicsSettingsLastNameDTO getLastName() {
        return lastName;
    }

    public void setLastName(DemographicsSettingsLastNameDTO lastName) {
        this.lastName = lastName;
    }

    public DemographicsSettingsMiddleNameDTO getMiddleName() {
        return middleName;
    }

    public void setMiddleName(DemographicsSettingsMiddleNameDTO middleName) {
        this.middleName = middleName;
    }

    public DemographicsSettingsDateOfBirthDTO getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DemographicsSettingsDateOfBirthDTO dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public DemographicsSettingsGenderDTO getGender() {
        return gender;
    }

    public void setGender(DemographicsSettingsGenderDTO gender) {
        this.gender = gender;
    }

    public DemographicsSettingsPrimaryRaceDTO getPrimaryRace() {
        return primaryRace;
    }

    public void setPrimaryRace(DemographicsSettingsPrimaryRaceDTO primaryRace) {
        this.primaryRace = primaryRace;
    }

    public DemographicsSettingsPrimaryRaceDTO getSecondaryRace() {
        return secondaryRace;
    }

    public void setSecondaryRace(DemographicsSettingsPrimaryRaceDTO secondaryRace) {
        this.secondaryRace = secondaryRace;
    }

    public DemographicsSettingsEthnicityDTO getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(DemographicsSettingsEthnicityDTO ethnicity) {
        this.ethnicity = ethnicity;
    }

    public DemographicsSettingsPreferredLanguageDTO getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(DemographicsSettingsPreferredLanguageDTO preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public DemographicsSettingsProfilePhotoDTO getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(DemographicsSettingsProfilePhotoDTO profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

}
