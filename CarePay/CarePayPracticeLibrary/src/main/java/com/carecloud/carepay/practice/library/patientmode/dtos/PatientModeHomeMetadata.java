
package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class PatientModeHomeMetadata {

    @SerializedName("labels")
    @Expose
    private PatientModeHomeLabels labels;
    @SerializedName("links")
    @Expose
    private PatientModeHomeLinks links;
    @SerializedName("transitions")
    @Expose
    private PatientModeHomeTransitionsDTO transitions;

    /**
     * 
     * @return
     *     The labels
     */
    public PatientModeHomeLabels getLabels() {
        return labels;
    }

    /**
     * 
     * @param labels
     *     The labels
     */
    public void setLabels(PatientModeHomeLabels labels) {
        this.labels = labels;
    }

    /**
     * 
     * @return
     *     The links
     */
    public PatientModeHomeLinks getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(PatientModeHomeLinks links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The transitions
     */
    public PatientModeHomeTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     * 
     * @param transitions
     *     The transitions
     */
    public void setTransitions(PatientModeHomeTransitionsDTO transitions) {
        this.transitions = transitions;
    }

}
