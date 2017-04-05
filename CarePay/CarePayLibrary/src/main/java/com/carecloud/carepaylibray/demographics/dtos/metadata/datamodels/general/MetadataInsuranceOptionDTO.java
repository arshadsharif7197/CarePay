package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 4/4/17.
 */

public class MetadataInsuranceOptionDTO extends MetadataOptionDTO {

    @SerializedName("payer_plans")
    @Expose
    private List<MetadataOptionDTO> payerPlans = new ArrayList<>();

    public List<MetadataOptionDTO> getPayerPlans() {
        return payerPlans;
    }

    public void setPayerPlans(List<MetadataOptionDTO> payerPlans) {
        this.payerPlans = payerPlans;
    }
}
