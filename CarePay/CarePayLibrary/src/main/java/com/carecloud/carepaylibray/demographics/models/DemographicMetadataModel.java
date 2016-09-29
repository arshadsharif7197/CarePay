package com.carecloud.carepaylibray.demographics.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 */
public class DemographicMetadataModel {
    @SerializedName("labels")
    @Expose
    private DemographicLabelsModel labels;

    @SerializedName("links")
    @Expose
    private DemographicLinksModel links;

    @SerializedName("transitions")
    @Expose
    private DemographicTransitionsModel transitions;

    /**
     *
     * @return
     * The labels
     */
    public DemographicLabelsModel getLabels() {
        return labels;
    }

    /**
     *
     * @param labels
     * The labels
     */
    public void setLabels(DemographicLabelsModel labels) {
        this.labels = labels;
    }

    /**
     *
     * @return
     * The links
     */
    public DemographicLinksModel getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinks(DemographicLinksModel links) {
        this.links = links;
    }

    /**
     *
     * @return
     * The transitions
     */
    public DemographicTransitionsModel getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions
     * The transitions
     */
    public void setTransitions(DemographicTransitionsModel transitions) {
        this.transitions = transitions;
    }
}
