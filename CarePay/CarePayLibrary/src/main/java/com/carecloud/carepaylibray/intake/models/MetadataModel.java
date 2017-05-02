package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetadataModel {

    @SerializedName("links")
    @Expose
    private LinksModel links = new LinksModel();
    @SerializedName("transitions")
    @Expose
    private TransitionsModel transitions = new TransitionsModel();
    @SerializedName("data_models")
    @Expose
    private DataModelsMetadataModel dataModels = new DataModelsMetadataModel();
    /**
     *
     * @return
     *     The dataModels
     */
    public DataModelsMetadataModel getDataModels() {
        return dataModels;
    }

    /**
     * 
     * @return
     *     The links
     */
    public LinksModel getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(LinksModel links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The transitions
     */
    public TransitionsModel getTransitions() {
        return transitions;
    }

    /**
     * 
     * @param transitions
     *     The transitions
     */
    public void setTransitions(TransitionsModel transitions) {
        this.transitions = transitions;
    }

}
