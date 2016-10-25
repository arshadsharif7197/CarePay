package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataPropertiesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'address'
 */

public class DemographicMetadataPropertiesAddressDTO extends MetadataPropertiesDTO {
    @SerializedName("phone") @Expose
    public MetadataEntityDTO phoneMetaDTO;

    @SerializedName("zipcode") @Expose
    public MetadataEntityDTO zipcodeMetaDTO;

    @SerializedName("address1") @Expose
    public MetadataEntityDTO address1MetaDTO;

    @SerializedName("address2") @Expose
    public MetadataEntityDTO address2MetaDTO;

    @SerializedName("city") @Expose
    public MetadataEntityDTO cityMetaDTO;

    @SerializedName("state") @Expose
    public MetadataEntityDTO stateMetaDTO;

    @SerializedName("country") @Expose
    public MetadataEntityDTO countryMetaDTO;
}