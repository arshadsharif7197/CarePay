package com.carecloud.carepaylibray.demographics.dtos.payload;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author pjohnson on 29/06/17.
 */

public class ConsentFormUserResponseListDTO {

    @Expose
    @SerializedName("consent_form_user_response")
    private List<ConsentFormUserResponseDTO> responses;

    public List<ConsentFormUserResponseDTO> getResponses() {
        return responses;
    }

    public void setResponses(List<ConsentFormUserResponseDTO> responses) {
        this.responses = responses;
    }
}
