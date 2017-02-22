package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.items;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemUpdateDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata DTO for 'updates' -> 'items' -> 'update'
 */
public class DemographicMetadataItemUpdateDTO {
    @SerializedName("update")
    @Expose
    public DemographicMetadataEntityItemUpdateDTO update = new DemographicMetadataEntityItemUpdateDTO();
}
