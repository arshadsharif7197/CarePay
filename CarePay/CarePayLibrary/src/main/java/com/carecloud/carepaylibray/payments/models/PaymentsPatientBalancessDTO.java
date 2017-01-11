package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rahul on 11/30/16.
 */

public class PaymentsPatientBalancessDTO implements Serializable {

    @SerializedName("metadata")
    @Expose
    private PaymentPayloadMetaDataDTO metadata;
    @SerializedName("payload")
    @Expose
    private List<PaymentPatientBalancesPayloadDTO> payload = new ArrayList<PaymentPatientBalancesPayloadDTO>();
    @SerializedName("pending_balances")
    @Expose
    private List<PatienceBalanceDTO> balances = new ArrayList<PatienceBalanceDTO>();

    @SerializedName("responsibility")
    @Expose
    private String pendingRepsonsibility;

    /**
     *
     * @return
     * The balances
     */
    public List<PatienceBalanceDTO> getBalances() {
        return balances;
    }

    /**
     *
     * @param balances
     * The balances
     */
    public void setBalances(List<PatienceBalanceDTO> balances) {
        this.balances = balances;
    }

    /**
     *
     * @return
     * The metadata
     */
    public PaymentPayloadMetaDataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PaymentPayloadMetaDataDTO metadata) {
        this.metadata = metadata;
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
}
