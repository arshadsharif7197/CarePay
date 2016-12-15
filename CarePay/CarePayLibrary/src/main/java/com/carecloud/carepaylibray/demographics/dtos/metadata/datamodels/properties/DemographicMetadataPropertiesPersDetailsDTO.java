package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'personal details'
 */

public class DemographicMetadataPropertiesPersDetailsDTO {
    @SerializedName("first_name") @Expose
    public MetadataEntityDTO firstName;

    @SerializedName("last_name") @Expose
    public MetadataEntityDTO lastName;

    @SerializedName("middle_name") @Expose
    public MetadataEntityDTO middleName;

    @SerializedName("date_of_birth") @Expose
    public MetadataEntityDTO dateOfBirth;

    @SerializedName("gender") @Expose
    public MetadataEntityDTO gender;

    @SerializedName("primary_race") @Expose
    public MetadataEntityDTO primaryRace;

    @SerializedName("secondary_race") @Expose
    public MetadataEntityDTO secondaryRace;

    @SerializedName("ethnicity") @Expose
    public MetadataEntityDTO ethnicity;

//  @SerializedName("preferred_language") @Expose
//  public MetadataEntityDTO preferredLanguage;

    @SerializedName("profile_photo") @Expose
    public MetadataEntityDTO profilePhoto;
}