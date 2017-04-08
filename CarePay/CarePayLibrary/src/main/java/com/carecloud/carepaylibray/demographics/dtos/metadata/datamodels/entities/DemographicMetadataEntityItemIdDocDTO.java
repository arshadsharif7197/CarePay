package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesIdDocDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * DTO for metadata property
 */
public class DemographicMetadataEntityItemIdDocDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    private DemographicMetadataPropertiesIdDocDTO properties = new DemographicMetadataPropertiesIdDocDTO();

    public DemographicMetadataPropertiesIdDocDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicMetadataPropertiesIdDocDTO properties) {
        this.properties = properties;
    }
}