package com.carecloud.carepay.patient.patientsplash.dtos;

/**
 * Created by Rahul on 11/7/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MetaDataDTO {

    @SerializedName("links")
    @Expose
    private LinksDTO links = new LinksDTO();
    @SerializedName("transitions")
    @Expose
    private TransitionsDTO transitions = new TransitionsDTO();

    /**
     * @return The links
     */
    public LinksDTO getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    public void setLinks(LinksDTO links) {
        this.links = links;
    }

    /**
     * @return The transitions
     */
    public TransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     * @param transitions The transitions
     */
    public void setTransitions(TransitionsDTO transitions) {
        this.transitions = transitions;
    }

}