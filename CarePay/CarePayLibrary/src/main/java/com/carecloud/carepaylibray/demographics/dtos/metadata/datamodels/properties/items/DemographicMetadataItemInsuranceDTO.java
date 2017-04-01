package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.properties.items;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/25/2016.
 * Specific metadata DTO for 'insurances' -> 'items' -> 'insurance'
 */
public class DemographicMetadataItemInsuranceDTO {
    @SerializedName("insurance")
    @Expose
    private DemographicMetadataEntityItemInsuranceDTO insurance = new DemographicMetadataEntityItemInsuranceDTO();

    public DemographicMetadataEntityItemInsuranceDTO getInsurance() {
        return insurance;
    }

    public void setInsurance(DemographicMetadataEntityItemInsuranceDTO insurance) {
        this.insurance = insurance;
    }
}
