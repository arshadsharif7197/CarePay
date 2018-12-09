package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.SerializedName;

public class DemographicsToggleOption extends DemographicsOption {

    @SerializedName("enabled")
    private boolean enabled = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
