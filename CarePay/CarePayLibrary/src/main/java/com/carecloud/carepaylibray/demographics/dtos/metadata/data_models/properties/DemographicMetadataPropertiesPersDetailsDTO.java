package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'personal details'
 */

public class DemographicMetadataPropertiesPersDetailsDTO {
    @SerializedName("first_name") @Expose
    public MetadataEntityDTO firstNameMetaDTO;

    @SerializedName("last_name") @Expose
    public MetadataEntityDTO lastNameMetaDTO;

    @SerializedName("middle_name") @Expose
    public MetadataEntityDTO middleNameMetaDTO;

    @SerializedName("date_of_birth") @Expose
    public MetadataEntityDTO dateOfBirthbMetaDTO;

    @SerializedName("gender") @Expose
    public MetadataEntityDTO genderMetaDTO;

    @SerializedName("primary_race") @Expose
    public MetadataEntityDTO primaryRaceMetaDTO;

    @SerializedName("secondary_race") @Expose
    public MetadataEntityDTO secRaceMetaDTO;

    @SerializedName("ethnicity") @Expose
    public MetadataEntityDTO ethnicityMetaDTO;

    @SerializedName("preferred_language") @Expose
    public MetadataEntityDTO prefLangMetaDTO;

    @SerializedName("profile_photo") @Expose
    public MetadataEntityDTO profPhotoMetaDTO;
}