package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.DemographicMetadataPropertiesIdDocDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * DTO for metadata property
 */
public class DemographicMetadataEntityItemIdDocDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    public DemographicMetadataPropertiesIdDocDTO properties;
}