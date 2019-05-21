package com.carecloud.carepay.practice.library.dobverification.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 4/4/19.
 */
public class DoBMetadata {

    @SerializedName("links")
    @Expose
    private DoBLinks links = new DoBLinks();
    @SerializedName("transitions")
    @Expose
    private DoBTransitions transitions = new DoBTransitions();

    public DoBLinks getLinks() {
        return links;
    }

    public void setLinks(DoBLinks links) {
        this.links = links;
    }

    public DoBTransitions getTransitions() {
        return transitions;
    }

    public void setTransitions(DoBTransitions transitions) {
        this.transitions = transitions;
    }
}
