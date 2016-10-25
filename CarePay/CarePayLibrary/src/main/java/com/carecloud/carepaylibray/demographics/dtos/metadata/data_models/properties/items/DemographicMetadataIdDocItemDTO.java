package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.items;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.DemographicMetadataPropertiesIdDocDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata for id doc
 */
public class DemographicMetadataIdDocItemDTO {
    @SerializedName("identity_document") @Expose
    public DemographicMetadataPropertiesIdDocDTO idDocMetadataDTO;
}
