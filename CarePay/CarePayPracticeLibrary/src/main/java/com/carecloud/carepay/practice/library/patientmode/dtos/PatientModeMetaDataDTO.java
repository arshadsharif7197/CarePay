package com.carecloud.carepay.practice.library.patientmode.dtos;

/**
 * Created by Rahul on 10/27/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeMetaDataDTO {

    @SerializedName("labels")
    @Expose
    private PatientModeLabelsDTO labels = new PatientModeLabelsDTO();
    @SerializedName("links")
    @Expose
    private PatientModeLinksDTO links = new PatientModeLinksDTO();
    @SerializedName("transitions")
    @Expose
    private PatientModeTransitionsDTO transitions = new PatientModeTransitionsDTO();

    /**
     * @return The labels
     */
    public PatientModeLabelsDTO getLabels() {
        return labels;
    }

    /**
     * @param labels The labels
     */
    public void setLabels(PatientModeLabelsDTO labels) {
        this.labels = labels;
    }

    /**
     * @return The links
     */
    public PatientModeLinksDTO getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    public void setLinks(PatientModeLinksDTO links) {
        this.links = links;
    }

    /**
     * @return The transitions
     */
    public PatientModeTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     * @param transitions The transitions
     */
    public void setTransitions(PatientModeTransitionsDTO transitions) {
        this.transitions = transitions;
    }

}