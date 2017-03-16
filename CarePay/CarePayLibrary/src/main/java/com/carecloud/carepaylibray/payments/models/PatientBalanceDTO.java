package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadInfoDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/30/16.
 */

public class PatientBalanceDTO implements Serializable {
    @SerializedName("demographics")
    @Expose
    private DemographicPayloadInfoDTO demographics = new DemographicPayloadInfoDTO();

    @SerializedName("responsibility")
    @Expose
    private String pendingRepsonsibility;

    @SerializedName("unapplied_credit")
    @Expose
    private String unappliedCredit;

    @SerializedName("pending_balances")
    @Expose
    private List<PendingBalanceDTO> balances = new ArrayList<>();

    @SerializedName("payload")
    @Expose
    private List<PaymentPatientBalancesPayloadDTO> payload = new ArrayList<>();

    /**
     *
     * @return
     * The balances
     */
    public List<PendingBalanceDTO> getBalances() {
        return balances;
    }

    /**
     *
     * @param balances
     * The balances
     */
    public void setBalances(List<PendingBalanceDTO> balances) {
        this.balances = balances;
    }

    /**
     *
     * @return
     * The payload
     */
    public List<PaymentPatientBalancesPayloadDTO> getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(List<PaymentPatientBalancesPayloadDTO> payload) {
        this.payload = payload;
    }

    public String getPendingRepsonsibility() {
        return pendingRepsonsibility;
    }

    public void setPendingRepsonsibility(String pendingRepsonsibility) {
        this.pendingRepsonsibility = pendingRepsonsibility;
    }

    public DemographicPayloadInfoDTO getDemographics() {
        return demographics;
    }

    public void setDemographics(DemographicPayloadInfoDTO demographics) {
        this.demographics = demographics;
    }

    public String getUnappliedCredit() {
        return unappliedCredit;
    }

    public void setUnappliedCredit(String unappliedCredit) {
        this.unappliedCredit = unappliedCredit;
    }
}
