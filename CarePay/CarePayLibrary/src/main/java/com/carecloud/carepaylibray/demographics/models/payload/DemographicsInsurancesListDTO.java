package com.carecloud.carepaylibray.demographics.models.payload;

import com.carecloud.carepaylibray.demographics.models.payload.DemographicInsurancePayloadDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Rahul on 10/7/16.
 */

public class DemographicsInsurancesListDTO {
    @SerializedName("insurances") @Expose
    private List<DemographicInsurancePayloadDTO> insurances;

    public List<DemographicInsurancePayloadDTO> getInsurances() {
        return insurances;
    }

    public void setInsurances(List<DemographicInsurancePayloadDTO> insurances) {
        this.insurances = insurances;
    }
}
