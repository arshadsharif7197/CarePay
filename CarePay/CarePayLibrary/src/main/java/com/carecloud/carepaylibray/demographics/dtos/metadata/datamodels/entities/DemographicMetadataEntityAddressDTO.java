package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.DemographicMetadataPropertiesAddressDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata entity for 'address'
 */
public class DemographicMetadataEntityAddressDTO extends MetadataEntityDTO {
    @SerializedName("properties") @Expose
    private DemographicMetadataPropertiesAddressDTO properties = new DemographicMetadataPropertiesAddressDTO();

    public DemographicMetadataPropertiesAddressDTO getProperties() {
        return properties;
    }

    public void setProperties(DemographicMetadataPropertiesAddressDTO properties) {
        this.properties = properties;
    }
}
