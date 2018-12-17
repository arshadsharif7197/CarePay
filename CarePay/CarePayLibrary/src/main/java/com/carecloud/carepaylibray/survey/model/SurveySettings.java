package com.carecloud.carepaylibray.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 10/09/18.
 */
public class SurveySettings {

    @Expose
    @SerializedName("satisfied_rate")
    private double satisfiedRate;
    @Expose
    @SerializedName("network_links")
    private SocialNetworkLinksDTO networkLinks = new SocialNetworkLinksDTO();

    public double getSatisfiedRate() {
        return satisfiedRate;
    }

    public void setSatisfiedRate(double satisfiedRate) {
        this.satisfiedRate = satisfiedRate;
    }

    public SocialNetworkLinksDTO getNetworkLinks() {
        return networkLinks;
    }

    public void setNetworkLinks(SocialNetworkLinksDTO networkLinks) {
        this.networkLinks = networkLinks;
    }
}
