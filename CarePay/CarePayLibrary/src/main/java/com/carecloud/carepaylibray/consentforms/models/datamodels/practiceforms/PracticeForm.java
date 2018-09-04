
package com.carecloud.carepaylibray.consentforms.models.datamodels.practiceforms;

import com.carecloud.carepaylibray.demographics.dtos.payload.ConsentFormUserResponseDTO;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PracticeForm implements Serializable {

    @SerializedName("payload")
    @Expose
    private JsonObject payload;

    @SerializedName("metadata")
    @Expose
    private PracticeFormMetadata metadata = new PracticeFormMetadata();

    private transient ConsentFormUserResponseDTO formUserResponseDTO;

    private String lastModifiedDate;

    private boolean selected;

    public JsonObject getPayload() {
        return payload;
    }

    public void setPayload(JsonObject payload) {
        this.payload = payload;
    }

    public PracticeFormMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(PracticeFormMetadata metadata) {
        this.metadata = metadata;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ConsentFormUserResponseDTO getFormUserResponseDTO() {
        return formUserResponseDTO;
    }

    public void setFormUserResponseDTO(ConsentFormUserResponseDTO formUserResponseDTO) {
        this.formUserResponseDTO = formUserResponseDTO;
    }
}
