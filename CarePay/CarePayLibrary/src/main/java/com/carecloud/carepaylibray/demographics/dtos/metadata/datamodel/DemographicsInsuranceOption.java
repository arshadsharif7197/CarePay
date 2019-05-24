package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 5/16/17
 */

public class DemographicsInsuranceOption extends DemographicsOption {

    @SerializedName("payer_plans")
    @Expose
    private List<DemographicsOption> payerPlans = new ArrayList<>();


    public List<DemographicsOption> getPayerPlans() {
        return payerPlans;
    }

    public void setPayerPlans(List<DemographicsOption> payerPlans) {
        this.payerPlans = payerPlans;
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName();
    }
}
