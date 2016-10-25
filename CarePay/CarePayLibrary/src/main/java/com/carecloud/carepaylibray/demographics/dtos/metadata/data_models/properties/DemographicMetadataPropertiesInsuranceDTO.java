package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.properties;

import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataEntityDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general.MetadataPropertiesDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific properties DTO for insurance.
 */

public class DemographicMetadataPropertiesInsuranceDTO extends MetadataPropertiesDTO {
    @SerializedName("insurance_photos") @Expose
    public MetadataEntityDTO insurancePhotosMetaDTO;

    @SerializedName("insurance_provider") @Expose
    public MetadataEntityDTO insuranceProviderMetaDTO;

    @SerializedName("insurance_plan") @Expose
    public MetadataEntityDTO insurancePlanMetaDTO;

    @SerializedName("insurance_member_id") @Expose
    public MetadataEntityDTO insuranceMemberIdMetaDTO;
}
