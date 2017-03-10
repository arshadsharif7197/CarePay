package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PatientBalanceDTO {
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoDTO demographics = new DemographicPayloadInfoDTO();

    @SerializedName("pending_balances")
    @Expose
    private List<PendingBalanceDTO> pendingBalances = new ArrayList<>();

    public List<PendingBalanceDTO> getPendingBalances() {
        return pendingBalances;
    }

    public void setPendingBalances(List<PendingBalanceDTO> pendingBalances) {
        this.pendingBalances = pendingBalances;
    }

    /**
     * @return demographics
     */
    public DemographicPayloadInfoDTO getDemographics() {
        return demographics;
    }
}
