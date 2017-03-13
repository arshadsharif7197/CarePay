package com.carecloud.carepaylibray.payments.models;

import com.carecloud.carepaylibray.appointments.models.AppointmentChargeDTO;
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
    private List<AppointmentChargeDTO> charges = new ArrayList<>();

    public List<AppointmentChargeDTO> getCharges() {
        return charges;
    }

    public void setCharges(List<AppointmentChargeDTO> charges) {
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
