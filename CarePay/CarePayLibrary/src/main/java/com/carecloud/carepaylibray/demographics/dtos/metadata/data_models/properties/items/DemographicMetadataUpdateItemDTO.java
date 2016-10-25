package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.items;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.DemographicMetadataPropertiesUpdateDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata DTO for 'updates' -> 'items' -> 'update'
 */
public class DemographicMetadataUpdateItemDTO {
    @SerializedName("update") @Expose
    public DemographicMetadataPropertiesUpdateDTO updateMetaDTO;
}
