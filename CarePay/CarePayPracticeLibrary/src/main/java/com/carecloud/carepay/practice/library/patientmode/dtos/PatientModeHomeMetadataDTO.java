
package com.carecloud.carepay.practice.library.patientmode.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientModeHomeMetadataDTO {

    @SerializedName("labels")
    @Expose
    private PatientModeHomeLabelsDTO labels;
    @SerializedName("links")
    @Expose
    private PatientModeHomeLinksDTO links;
    @SerializedName("transitions")
    @Expose
    private PatientModeHomeTransitionsDTO transitions;

    /**
     * 
     * @return
     *     The labels
     */
    public PatientModeHomeLabelsDTO getLabels() {
        return labels;
    }

    /**
     * 
     * @param labels
     *     The labels
     */
    public void setLabels(PatientModeHomeLabelsDTO labels) {
        this.labels = labels;
    }

    /**
     * 
     * @return
     *     The links
     */
    public PatientModeHomeLinksDTO getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(PatientModeHomeLinksDTO links) {
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
