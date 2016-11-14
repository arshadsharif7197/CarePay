package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Model for demographics transition.
 */
public class DemographicTransitionsDTO {
    @SerializedName("confirm_demographics")
    @Expose
    private TransitionDTO confirmDemographics;

    @SerializedName("update_demographics")
    @Expose
    private TransitionDTO updateDemographics;

    /**
     *
     * @return
     * The confirmDemographics
     */
    public TransitionDTO getConfirmDemographics() {
        return confirmDemographics;
    }

    /**
     *
     * @param confirmDemographics
     * The confirm_demographics
     */
    public void setConfirmDemographics(TransitionDTO confirmDemographics) {
        this.confirmDemographics = confirmDemographics;
    }


    /**
     * @return The updateDemographics
     */
    public TransitionDTO getUpdateDemographics() {
        return updateDemographics;
    }

    /**
     * @param updateDemographics The update_demographics
     */
    public void setUpdateDemographics(TransitionDTO updateDemographics) {
        this.updateDemographics = updateDemographics;
    }
}
