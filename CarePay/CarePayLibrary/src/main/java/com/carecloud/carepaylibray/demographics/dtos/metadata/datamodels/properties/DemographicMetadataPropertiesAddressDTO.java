package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'address'
 */

public class DemographicMetadataPropertiesAddressDTO {
    @SerializedName("phone")
    @Expose
    public MetadataEntityDTO phone = new MetadataEntityDTO();

    @SerializedName("zipcode")
    @Expose
    public MetadataEntityDTO zipcode = new MetadataEntityDTO();

    @SerializedName("address1")
    @Expose
    public MetadataEntityDTO address1 = new MetadataEntityDTO();

    @SerializedName("address2")
    @Expose
    public MetadataEntityDTO address2 = new MetadataEntityDTO();

    @SerializedName("city")
    @Expose
    public MetadataEntityDTO city = new MetadataEntityDTO();

    @SerializedName("state")
    @Expose
    public MetadataEntityDTO state = new MetadataEntityDTO();

    @SerializedName("country")
    @Expose
    public MetadataEntityDTO country = new MetadataEntityDTO();
}