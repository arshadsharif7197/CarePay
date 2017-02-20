package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.lists;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.items.DemographicMetadataItemIdDocDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'identity document'
 */

public class DemographicMetadataPropertiesListIdDocDTO {
    @SerializedName("items")
    @Expose
    public DemographicMetadataItemIdDocDTO items = new DemographicMetadataItemIdDocDTO(); // expected to be of type DemographicMetadataPropertiesIdDocDTO
}
