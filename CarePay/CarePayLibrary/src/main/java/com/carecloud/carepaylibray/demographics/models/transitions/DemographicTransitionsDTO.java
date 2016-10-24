package com.carecloud.carepaylibray.demographics.models.transitions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Model for demographics transition.
 */
public class DemographicTransitionsDTO {
    @SerializedName("confirm_demographics")
    @Expose
    private DemographicTransitionDTO confirmDemographics;

    /**
     *
     * @return
     * The confirmDemographics
     */
    public DemographicTransitionDTO getConfirmDemographics() {
        return confirmDemographics;
    }

    /**
     *
     * @param confirmDemographics
     * The confirm_demographics
     */
    public void setConfirmDemographics(DemographicTransitionDTO confirmDemographics) {
        this.confirmDemographics = confirmDemographics;
    }
}
