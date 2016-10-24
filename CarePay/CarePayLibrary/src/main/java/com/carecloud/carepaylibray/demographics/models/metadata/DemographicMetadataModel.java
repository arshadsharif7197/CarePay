package com.carecloud.carepaylibray.demographics.models.metadata;

import com.carecloud.carepaylibray.demographics.models.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.models.links.DemographicLinksDTO;
import com.carecloud.carepaylibray.demographics.models.transitions.DemographicTransitionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Model for meta-data.
 */
class DemographicMetadataModel {
    @SerializedName("labels")
    @Expose
    private DemographicLabelsDTO labels;

    @SerializedName("links")
    @Expose
    private DemographicLinksDTO links;

    @SerializedName("transitions")
    @Expose
    private DemographicTransitionsDTO transitions;

    /**
     *
     * @return
     * The labels
     */
    public DemographicLabelsDTO getLabels() {
        return labels;
    }

    /**
     *
     * @param labels
     * The labels
     */
    public void setLabels(DemographicLabelsDTO labels) {
        this.labels = labels;
    }

    /**
     *
     * @return
     * The links
     */
    public DemographicLinksDTO getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinks(DemographicLinksDTO links) {
        this.links = links;
    }

    /**
     *
     * @return
     * The transitions
     */
    public DemographicTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions
     * The transitions
     */
    public void setTransitions(DemographicTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
