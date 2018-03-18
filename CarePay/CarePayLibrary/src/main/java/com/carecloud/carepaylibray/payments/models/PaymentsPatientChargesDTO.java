package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 05/01/17.
 */

public class PaymentsPatientChargesDTO {
    @SerializedName("metadata")
    @Expose
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();
    @SerializedName("payload")
    @Expose
    private List<BalanceItemDTO> charges = new ArrayList<>();

    public List<BalanceItemDTO> getCharges() {
        return charges;
    }

    public void setCharges(List<BalanceItemDTO> charges) {
        this.charges = charges;
    }

    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    /**
     * Validate Metadata
     * @return true if metadata is valid
     */
    public boolean hasValidMetadata(){
        return metadata.isValid();
    }
}
