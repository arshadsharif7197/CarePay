package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.verifydemotransitions;

/**
 * Created by Rahul on 10/31/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyDemographicTransitionsDTO {

    @SerializedName("update_demographics")
    @Expose
    private UpdateDemographicDTO updateDemographics = new UpdateDemographicDTO();

    /**
     * @return The updateDemographics
     */
    public UpdateDemographicDTO getUpdateDemographics() {
        return updateDemographics;
    }

    /**
     * @param updateDemographics The update_demographics
     */
    public void setUpdateDemographics(UpdateDemographicDTO updateDemographics) {
        this.updateDemographics = updateDemographics;
    }

}