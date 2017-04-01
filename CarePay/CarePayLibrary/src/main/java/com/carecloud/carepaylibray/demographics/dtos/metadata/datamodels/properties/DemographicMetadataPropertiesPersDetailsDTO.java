package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'personal details'
 */

public class DemographicMetadataPropertiesPersDetailsDTO {

    @SerializedName("first_name")
    @Expose
    private MetadataEntityDTO firstName = new MetadataEntityDTO();

    @SerializedName("last_name")
    @Expose
    private MetadataEntityDTO lastName = new MetadataEntityDTO();

    @SerializedName("middle_name")
    @Expose
    private MetadataEntityDTO middleName = new MetadataEntityDTO();

    @SerializedName("date_of_birth")
    @Expose
    private MetadataEntityDTO dateOfBirth = new MetadataEntityDTO();

    @SerializedName("gender")
    @Expose
    private MetadataEntityDTO gender = new MetadataEntityDTO();

    @SerializedName("primary_race")
    @Expose
    private MetadataEntityDTO primaryRace = new MetadataEntityDTO();

    @SerializedName("secondary_race")
    @Expose
    private MetadataEntityDTO secondaryRace = new MetadataEntityDTO();

    @SerializedName("ethnicity")
    @Expose
    private MetadataEntityDTO ethnicity = new MetadataEntityDTO();

    @SerializedName("profile_photo")
    @Expose
    private MetadataEntityDTO profilePhoto = new MetadataEntityDTO();

    public MetadataEntityDTO getFirstName() {
        return firstName;
    }

    public void setFirstName(MetadataEntityDTO firstName) {
        this.firstName = firstName;
    }

    public MetadataEntityDTO getLastName() {
        return lastName;
    }

    public void setLastName(MetadataEntityDTO lastName) {
        this.lastName = lastName;
    }

    public MetadataEntityDTO getMiddleName() {
        return middleName;
    }

    public void setMiddleName(MetadataEntityDTO middleName) {
        this.middleName = middleName;
    }

    public MetadataEntityDTO getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(MetadataEntityDTO dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public MetadataEntityDTO getGender() {
        return gender;
    }

    public void setGender(MetadataEntityDTO gender) {
        this.gender = gender;
    }

    public MetadataEntityDTO getPrimaryRace() {
        return primaryRace;
    }

    public void setPrimaryRace(MetadataEntityDTO primaryRace) {
        this.primaryRace = primaryRace;
    }

    public MetadataEntityDTO getSecondaryRace() {
        return secondaryRace;
    }

    public void setSecondaryRace(MetadataEntityDTO secondaryRace) {
        this.secondaryRace = secondaryRace;
    }

    public MetadataEntityDTO getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(MetadataEntityDTO ethnicity) {
        this.ethnicity = ethnicity;
    }

    public MetadataEntityDTO getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(MetadataEntityDTO profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}