package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models;

import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.links.DemographicLinksDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.DemographicTransitionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Master DTO for demographics meta-data
 */
public class DemographicMetadataDTO {
    @SerializedName("labels") @Expose
    private DemographicLabelsDTO labels;

    @SerializedName("links") @Expose
    private DemographicLinksDTO links;

    @SerializedName("transitions") @Expose
    private DemographicTransitionsDTO transitions;

    @SerializedName("data_models") @Expose
    private DemographicMetadataDataModelsDTO dataModels;

    /**
     *
     * @return The labels
     */
    public DemographicLabelsDTO getLabels() {
        return labels;
    }

    /**
     *
     * @param labels The labels
     */
    public void setLabels(DemographicLabelsDTO labels) {
        this.labels = labels;
    }

    /**
     *
     * @return The links
     */
    public DemographicLinksDTO getLinks() {
        return links;
    }

    /**
     *
     * @param links the links
     */
    public void setLinks(DemographicLinksDTO links) {
        this.links = links;
    }

    /**
     *
     * @return The transitions
     */
    public DemographicTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions The transitions
     */
    public void setTransitions(DemographicTransitionsDTO transitions) {
        this.transitions = transitions;
    }

    /**
     * Getter.
     * @return demographics metadata entities
     */
    public DemographicMetadataDataModelsDTO getDataModels() {
        return dataModels;
    }

    /**
     * Setter.
     * @param dataModels The demographics metadata entities
     */
    public void setDataModels(DemographicMetadataDataModelsDTO dataModels) {
        this.dataModels = dataModels;
    }
}
