package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties;


import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataPropertiesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataPropertiesListObjectDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'insurance'
 */

public class DemographicMetadataPropertiesListInsuranceDTO extends MetadataPropertiesListObjectDTO {
    @SerializedName("insurance") @Expose
    public MetadataPropertiesDTO insuranceMetaDTO;
}
