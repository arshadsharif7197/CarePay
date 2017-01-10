package com.carecloud.carepaylibray.demographicsettings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/6/2017.
 */

public class DemographicsSettingsPersonalDetailsPayloadDTO {
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("primary_race")
    @Expose
    private String primaryRace;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("ethnicity")
    @Expose
    private String ethnicity;
    @SerializedName("middle_name")
    @Expose
    private String middleName;
    @SerializedName("secondary_race")
    @Expose
    private String secondaryRace;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("profile_photo")
    @Expose
    private String profilePhoto;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPrimaryRace() {
        return primaryRace;
    }

    public void setPrimaryRace(String primaryRace) {
        this.primaryRace = primaryRace;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSecondaryRace() {
        return secondaryRace;
    }

    public void setSecondaryRace(String secondaryRace) {
        this.secondaryRace = secondaryRace;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

}
