package com.carecloud.carepay.practice.library.checkin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class PatientBalanceDTO {
    @SerializedName("metadata")
    @Expose
    private PatientBalanceMetadataDTO metadata;
    @SerializedName("payload")
    @Expose
    private List<PatientBalancePayloadDTO> payload = new ArrayList<PatientBalancePayloadDTO>();

    @SerializedName("pending_balances")
    @Expose
    private List<PendingBalanceDTO> pendingBalances = null;

    public List<PendingBalanceDTO> getPendingBalances() {
        return pendingBalances;
    }

    public void setPendingBalances(List<PendingBalanceDTO> pendingBalances) {
        this.pendingBalances = pendingBalances;
    }

    /**
     *
     * @return
     * The metadata
     */
    public PatientBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata
     * The metadata
     */
    public void setMetadata(PatientBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     *
     * @return
     * The payload
     */
    public List<PatientBalancePayloadDTO> getPayload() {
        return payload;
    }

    /**
     *
     * @param payload
     * The payload
     */
    public void setPayload(List<PatientBalancePayloadDTO> payload) {
        this.payload = payload;
    }
}
