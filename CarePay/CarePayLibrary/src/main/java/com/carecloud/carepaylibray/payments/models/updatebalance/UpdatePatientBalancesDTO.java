package com.carecloud.carepaylibray.payments.models.updatebalance;

import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsDemographicsDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 2/22/17.
 */

public class UpdatePatientBalancesDTO {

    @SerializedName("pending_balances")
    @Expose
    private List<PendingBalanceDTO> balances = new ArrayList<>();
    @SerializedName("demographics")
    @Expose
    private DemographicsSettingsDemographicsDTO demographics = new DemographicsSettingsDemographicsDTO();
    @SerializedName("responsibility")
    @Expose
    private String pendingRepsonsibility;

    public List<PendingBalanceDTO> getBalances() {
        return balances;
    }

    public void setBalances(List<PendingBalanceDTO> balances) {
        this.balances = balances;
    }

    public DemographicsSettingsDemographicsDTO getDemographics() {
        return demographics;
    }

    public String getPendingRepsonsibility() {
        return pendingRepsonsibility;
    }

    public void setPendingRepsonsibility(String pendingRepsonsibility) {
        this.pendingRepsonsibility = pendingRepsonsibility;
    }
}
