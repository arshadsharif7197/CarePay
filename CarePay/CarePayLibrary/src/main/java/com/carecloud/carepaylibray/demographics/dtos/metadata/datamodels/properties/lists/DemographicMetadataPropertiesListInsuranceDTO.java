package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.lists;


import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.items.DemographicMetadataItemInsuranceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * Specific metadata DTO for 'insurances' -> 'items'
 */

public class DemographicMetadataPropertiesListInsuranceDTO {
    @SerializedName("items")
    @Expose
    private DemographicMetadataItemInsuranceDTO items = new DemographicMetadataItemInsuranceDTO();

    public DemographicMetadataItemInsuranceDTO getItems() {
        return items;
    }

    public void setItems(DemographicMetadataItemInsuranceDTO items) {
        this.items = items;
    }
}
