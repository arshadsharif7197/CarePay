package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.DemographicMetadataPropertiesUpdateDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * DTO for an entity holding properties of an update
 */
public class DemographicMetadataEntityItemUpdateDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    public DemographicMetadataPropertiesUpdateDTO update;
}