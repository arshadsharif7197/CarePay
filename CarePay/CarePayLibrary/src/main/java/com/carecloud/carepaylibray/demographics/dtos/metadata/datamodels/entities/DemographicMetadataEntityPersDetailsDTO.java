package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesPersDetailsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata DTO for
 */
public class DemographicMetadataEntityPersDetailsDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    private DemographicMetadataPropertiesPersDetailsDTO properties = new DemographicMetadataPropertiesPersDetailsDTO();

    public DemographicMetadataPropertiesPersDetailsDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicMetadataPropertiesPersDetailsDTO properties) {
        this.properties = properties;
    }
}
