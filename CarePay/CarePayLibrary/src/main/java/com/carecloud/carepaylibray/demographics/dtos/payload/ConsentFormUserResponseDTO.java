package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 29/06/17.
 */

public class ConsentFormUserResponseDTO {

    @Expose
    @SerializedName("form_uuid")
    private String formId;

    @Expose
    @SerializedName("response")
    private JsonObject response;

    @Expose
    @SerializedName("metadata")
    private JsonObject metadata;

    @Expose
    @SerializedName("updated_dt")
    private String updateDate;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public JsonObject getResponse() {
        return response;
    }

    public void setResponse(JsonObject response) {
        this.response = response;
    }

    public JsonObject getMetadata() {
        return metadata;
    }

    public void setMetadata(JsonObject metadata) {
        this.metadata = metadata;
    }

    public String getUpdatedDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
