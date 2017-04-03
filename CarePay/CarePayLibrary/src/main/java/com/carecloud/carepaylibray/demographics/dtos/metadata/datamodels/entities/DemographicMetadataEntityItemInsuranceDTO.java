package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesInsuranceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * DTO for an entity holding properties of an insurance item
 */
public class DemographicMetadataEntityItemInsuranceDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    private DemographicMetadataPropertiesInsuranceDTO properties = new DemographicMetadataPropertiesInsuranceDTO();

    public DemographicMetadataPropertiesInsuranceDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicMetadataPropertiesInsuranceDTO properties) {
        this.properties = properties;
    }
}
