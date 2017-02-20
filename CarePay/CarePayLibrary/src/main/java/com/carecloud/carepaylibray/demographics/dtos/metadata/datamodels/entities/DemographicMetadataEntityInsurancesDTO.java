package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.lists.DemographicMetadataPropertiesListInsuranceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata properties list for 'insurance'
 */
public class DemographicMetadataEntityInsurancesDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    public DemographicMetadataPropertiesListInsuranceDTO properties = new DemographicMetadataPropertiesListInsuranceDTO();
}
