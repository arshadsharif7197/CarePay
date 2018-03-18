package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicSettingsCurrentPasswordDTO {

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