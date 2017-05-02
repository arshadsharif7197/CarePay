package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.lists.DemographicMetadataPropertiesListIdDocDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata for list of id docs
 */
public class DemographicMetadataEntityIdDocsDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    private DemographicMetadataPropertiesListIdDocDTO properties = new DemographicMetadataPropertiesListIdDocDTO();

    public DemographicMetadataPropertiesListIdDocDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicMetadataPropertiesListIdDocDTO properties) {
        this.properties = properties;
    }
}