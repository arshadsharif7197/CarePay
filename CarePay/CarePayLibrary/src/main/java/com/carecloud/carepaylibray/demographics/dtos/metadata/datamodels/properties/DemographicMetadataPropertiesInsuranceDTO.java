package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataEntityDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific properties DTO for insurance.
 */

public class DemographicMetadataPropertiesInsuranceDTO {
    @SerializedName("insurance_photos") @Expose
    public MetadataEntityDTO insurancePhotos;

    @SerializedName("insurance_provider") @Expose
    public MetadataEntityDTO insuranceProvider;

    @SerializedName("insurance_plan") @Expose
    public MetadataEntityDTO insurancePlan;

    @SerializedName("insurance_member_id") @Expose
    public MetadataEntityDTO insuranceMemberId;

    @SerializedName("insurance_type") @Expose
    public MetadataEntityDTO insuranceType;
}
