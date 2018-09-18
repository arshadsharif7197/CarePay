package com.carecloud.carepaylibray.consentforms.models;

import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2/08/18.
 */
public class UserFormDTO {

    @Expose
    @SerializedName("metadata")
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();

    @Expose
    @SerializedName("historical_forms")
    private PendingFormsDTO historyForms = new PendingFormsDTO();

    @Expose
    @SerializedName("pending_forms")
    private PendingFormsDTO pendingForms = new PendingFormsDTO();

    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }

    public PendingFormsDTO getPendingForms() {
        return pendingForms;
    }

    public void setPendingForms(PendingFormsDTO pendingForms) {
        this.pendingForms = pendingForms;
    }

    public PendingFormsDTO getHistoryForms() {
        return historyForms;
    }

    public void setHistoryForms(PendingFormsDTO historyForms) {
        this.historyForms = historyForms;
    }
}
