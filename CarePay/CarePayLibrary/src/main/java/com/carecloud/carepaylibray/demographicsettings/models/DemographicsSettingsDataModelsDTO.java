package com.carecloud.carepaylibray.demographicsettings.models;

/**
 * Created by harshal_patil on 1/5/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DemographicsSettingsDataModelsDTO {

    @SerializedName("demographic")
    @Expose
    private DemographicsSettingsDetailsDTO demographic = new DemographicsSettingsDetailsDTO();

    public DemographicsSettingsDetailsDTO getDemographic() {
        return demographic;
    }

    public void setDemographic(DemographicsSettingsDetailsDTO demographic) {
        this.demographic = demographic;
    }

}