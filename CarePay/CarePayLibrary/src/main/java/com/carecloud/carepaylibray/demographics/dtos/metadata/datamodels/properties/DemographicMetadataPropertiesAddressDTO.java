package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'address'
 */

public class DemographicMetadataPropertiesAddressDTO {
    @SerializedName("phone") @Expose
    public MetadataEntityDTO phone;

    @SerializedName("zipcode") @Expose
    public MetadataEntityDTO zipcode;

    @SerializedName("address1") @Expose
    public MetadataEntityDTO address1;

    @SerializedName("address2") @Expose
    public MetadataEntityDTO address2;

    @SerializedName("city") @Expose
    public MetadataEntityDTO city;

    @SerializedName("state") @Expose
    public MetadataEntityDTO state;

    @SerializedName("country") @Expose
    public MetadataEntityDTO country;
}