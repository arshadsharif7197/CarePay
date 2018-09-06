package com.carecloud.carepaylibray.consentforms.models;

import com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms.PracticeForm;
import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalanceMetadataDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 2/08/18.
 */
public class PendingFormDTO {

    @Expose
    @SerializedName("form")
    private PracticeForm form = new PracticeForm();

    @Expose
    @SerializedName("payload")
    private ConsentFormUserResponseDTO payload = new ConsentFormUserResponseDTO();

    @Expose
    @SerializedName("metadata")
    private PendingBalanceMetadataDTO metadata = new PendingBalanceMetadataDTO();

    public PracticeForm getForm() {
        return form;
    }

    public void setForm(PracticeForm form) {
        this.form = form;
    }

    public ConsentFormUserResponseDTO getPayload() {
        return payload;
    }

    public void setPayload(ConsentFormUserResponseDTO payload) {
        this.payload = payload;
    }

    public PendingBalanceMetadataDTO getMetadata() {
        return metadata;
    }

    public void setMetadata(PendingBalanceMetadataDTO metadata) {
        this.metadata = metadata;
    }
}
