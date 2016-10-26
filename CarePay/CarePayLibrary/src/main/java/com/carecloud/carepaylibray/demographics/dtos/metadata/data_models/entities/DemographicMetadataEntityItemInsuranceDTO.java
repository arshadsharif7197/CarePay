package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * DTO for an entity holding properties of an insurance item
 */
public class DemographicMetadataEntityItemInsuranceDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    public DemographicMetadataPropertiesInsuranceDTO properties;
}