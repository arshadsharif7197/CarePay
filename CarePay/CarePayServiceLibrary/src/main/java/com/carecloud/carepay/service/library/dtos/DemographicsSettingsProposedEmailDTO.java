package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by harshal_patil on 1/21/2017.
 */

public class DemographicsSettingsProposedEmailDTO {
    @SerializedName("type")
    @Expose
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
