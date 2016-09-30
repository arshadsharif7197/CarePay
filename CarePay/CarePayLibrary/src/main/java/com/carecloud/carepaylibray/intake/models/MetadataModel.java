package com.carecloud.carepaylibray.intake.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetadataModel {

    @SerializedName("label")
    @Expose
    private LabelModel label;
    @SerializedName("links")
    @Expose
    private LinksModel links;
    @SerializedName("transitions")
    @Expose
    private TransitionsModel transitions;

    /**
     * 
     * @return
     *     The label
     */
    public LabelModel getLabel() {
        return label;
    }

    /**
     * 
     * @param label
     *     The label
     */
    public void setLabel(LabelModel label) {
        this.label = label;
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
