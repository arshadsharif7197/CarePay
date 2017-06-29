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
    private JsonObject response;

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
}
