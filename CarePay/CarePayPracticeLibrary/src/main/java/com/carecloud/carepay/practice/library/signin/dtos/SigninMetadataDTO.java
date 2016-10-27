package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */

public class SigninMetadataDTO {
    @SerializedName("labels")
    @Expose
    private SigninLabelsDTO labels;
    @SerializedName("links")
    @Expose
    private SigninLinksDTO links;
    @SerializedName("transitions")
    @Expose
    private SignInTransitionsDTO transitions;

    /**
     *
     * @return
     * The labels
     */
    public SigninLabelsDTO getLabels() {
        return labels;
    }

    /**
     *
     * @param labels
     * The labels
     */
    public void setLabels(SigninLabelsDTO labels) {
        this.labels = labels;
    }

    /**
     *
     * @return
     * The links
     */
    public SigninLinksDTO getLinks() {
        return links;
    }

    /**
     *
     * @param links
     * The links
     */
    public void setLinks(SigninLinksDTO links) {
        this.links = links;
    }

    /**
     *
     * @return
     * The transitions
     */
    public SignInTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions
     * The transitions
     */
    public void setTransitions(SignInTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
