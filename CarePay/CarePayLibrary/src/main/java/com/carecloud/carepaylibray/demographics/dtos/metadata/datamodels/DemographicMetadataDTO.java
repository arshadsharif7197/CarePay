package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels;

import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel.DemographicDataModel;
import com.carecloud.carepaylibray.demographics.dtos.metadata.links.DemographicLinksDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.DemographicTransitionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Master DTO for demographics meta-data
 */
public class DemographicMetadataDTO {
    @SerializedName("links")
    @Expose
    private DemographicLinksDTO links = new DemographicLinksDTO();

    @SerializedName("transitions")
    @Expose
    private DemographicTransitionsDTO transitions = new DemographicTransitionsDTO();

    @SerializedName("data_models")
    private DemographicDataModel newDataModel = new DemographicDataModel();

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

    public DemographicDataModel getNewDataModel() {
        return newDataModel;
    }

    public void setNewDataModel(DemographicDataModel newDataModel) {
        this.newDataModel = newDataModel;
    }
}
