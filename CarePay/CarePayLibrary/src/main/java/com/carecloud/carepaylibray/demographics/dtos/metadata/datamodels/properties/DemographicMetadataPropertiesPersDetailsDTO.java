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
    public MetadataEntityDTO firstName = new MetadataEntityDTO();

    @SerializedName("last_name")
    @Expose
    public MetadataEntityDTO lastName = new MetadataEntityDTO();

    @SerializedName("middle_name")
    @Expose
    public MetadataEntityDTO middleName = new MetadataEntityDTO();

    @SerializedName("date_of_birth")
    @Expose
    public MetadataEntityDTO dateOfBirth = new MetadataEntityDTO();

    @SerializedName("gender")
    @Expose
    public MetadataEntityDTO gender = new MetadataEntityDTO();

    @SerializedName("primary_race")
    @Expose
    public MetadataEntityDTO primaryRace = new MetadataEntityDTO();

    @SerializedName("secondary_race")
    @Expose
    public MetadataEntityDTO secondaryRace = new MetadataEntityDTO();

    @SerializedName("ethnicity")
    @Expose
    public MetadataEntityDTO ethnicity = new MetadataEntityDTO();

//    @SerializedName("preferred_language") @Expose
//    public MetadataEntityDTO preferredLanguage;

    @SerializedName("profile_photo")
    @Expose
    public MetadataEntityDTO profilePhoto = new MetadataEntityDTO();
}