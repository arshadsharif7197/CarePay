package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO holding various metadata entities
 */
public class DemographicMetadataDataModelsDTO {
    @SerializedName("demographic") @Expose
    public DemographicMetadataEntitiesDTO demographic;
}
