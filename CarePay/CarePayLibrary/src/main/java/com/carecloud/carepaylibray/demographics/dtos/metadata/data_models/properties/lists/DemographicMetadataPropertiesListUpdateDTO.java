package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.lists;


import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.items.DemographicMetadataItemUpdateDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'Update'
 */

public class DemographicMetadataPropertiesListUpdateDTO {
    @SerializedName("items") @Expose
    public DemographicMetadataItemUpdateDTO items; // expected just a 'type'
}