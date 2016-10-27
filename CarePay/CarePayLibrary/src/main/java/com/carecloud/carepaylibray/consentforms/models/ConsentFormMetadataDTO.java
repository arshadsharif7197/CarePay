package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */

import com.carecloud.carepaylibray.consentforms.models.labels.ConsentFormLabelsDTO;
import com.carecloud.carepaylibray.consentforms.models.links.ConsentFormLinksDTO;
import com.carecloud.carepaylibray.consentforms.models.transitions.ConsentFormTransitionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConsentFormMetadataDTO {

    @SerializedName("label")
    @Expose
    private ConsentFormLabelsDTO label;
    @SerializedName("links")
    @Expose
    private ConsentFormLinksDTO links;
    @SerializedName("transitions")
    @Expose
    private ConsentFormTransitionsDTO transitions;
    @SerializedName("data_models")
    @Expose
    private ConsentFormDataModelDTO dataModels;

    /**
     * @return The label
     */
    public ConsentFormLabelsDTO getLabel() {
        return label;
    }

    /**
     * @param label The label
     */
    public void setLabel(ConsentFormLabelsDTO label) {
        this.label = label;
    }

    /**
     * @return The links
     */
    public ConsentFormLinksDTO getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    public void setLinks(ConsentFormLinksDTO links) {
        this.links = links;
    }

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

}