package com.carecloud.carepaylibray.demographics.models;
import com.carecloud.carepaylibray.base.models.BaseTransitionsModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicTransitionsModel {
    @SerializedName("confirm_demographics")
    @Expose
    private BaseTransitionsModel confirmDemographics;

    /**
     *
     * @return
     * The confirmDemographics
     */
    public BaseTransitionsModel getConfirmDemographics() {
        return confirmDemographics;
    }

    /**
     *
     * @param confirmDemographics
     * The confirm_demographics
     */
    public void setConfirmDemographics(BaseTransitionsModel confirmDemographics) {
        this.confirmDemographics = confirmDemographics;
    }
}
