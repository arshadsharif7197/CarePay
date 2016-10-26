package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties.items;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata DTO for 'insurances' -> 'items' -> 'insurance'
 */
public class DemographicMetadataItemInsuranceDTO {
    @SerializedName("insurance") @Expose
    public DemographicMetadataEntityItemInsuranceDTO insurance;
}
