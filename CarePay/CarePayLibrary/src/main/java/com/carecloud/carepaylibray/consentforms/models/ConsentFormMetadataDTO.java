package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16
 */

import com.carecloud.carepaylibray.consentforms.models.transitions.ConsentFormTransitionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormMetadataDTO {

    @SerializedName("transitions")
    @Expose
    private ConsentFormTransitionsDTO transitions = new ConsentFormTransitionsDTO();
    @SerializedName("data_models")
    @Expose
    private ConsentFormDataModelDTO dataModels = new ConsentFormDataModelDTO();
    @SerializedName("links")
    @Expose
    private ConsentFormsLinksDTO links = new ConsentFormsLinksDTO();

    /**
     * @return The transitions
     */
    public ConsentFormTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     * @param transitions The transitions
     */
    public void setTransitions(ConsentFormTransitionsDTO transitions) {
        this.transitions = transitions;
    }

    /**
     * @return The dataModels
     */
    public ConsentFormDataModelDTO getDataModels() {
        return dataModels;
    }

    /**
     * @param dataModels The data_models
     */
    public void setDataModels(ConsentFormDataModelDTO dataModels) {
        this.dataModels = dataModels;
    }

    public ConsentFormsLinksDTO getLinks() {
        return links;
    }

    public void setLinks(ConsentFormsLinksDTO links) {
        this.links = links;
    }
}