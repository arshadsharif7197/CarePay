
package com.carecloud.carepay.practice.library.signin.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SigninPatientModeMetadataDTO {

    @SerializedName("labels")
    @Expose
    private SigninPatientModeLabelsDTO labels;
    @SerializedName("links")
    @Expose
    private SigninPatientModeLinksDTO links;
    @SerializedName("transitions")
    @Expose
    private SigninPatientModeTransitionsDTO transitions;
    @SerializedName("data_models")
    @Expose
    private SignInPatientModeDataModelDTO dataModels;

    /**
     * 
     * @return
     *     The labels
     */
    public SigninPatientModeLabelsDTO getLabels() {
        return labels;
    }

    /**
     * 
     * @param labels
     *     The labels
     */
    public void setLabels(SigninPatientModeLabelsDTO labels) {
        this.labels = labels;
    }

    /**
     * 
     * @return
     *     The links
     */
    public SigninPatientModeLinksDTO getLinks() {
        return links;
    }

    /**
     * 
     * @param links
     *     The links
     */
    public void setLinks(SigninPatientModeLinksDTO links) {
        this.links = links;
    }

    /**
     * 
     * @return
     *     The transitions
     */
    public SigninPatientModeTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     * 
     * @param transitions
     *     The transitions
     */
    public void setTransitions(SigninPatientModeTransitionsDTO transitions) {
        this.transitions = transitions;
    }

    /**
     * @return The dataModels
     */
    public SignInPatientModeDataModelDTO getLoginDataModels() {
        return dataModels;
    }

    /**
     * @param dataModels The data_models
     */
    public void setLoginDataModels(SignInPatientModeDataModelDTO dataModels) {
        this.dataModels = dataModels;
    }

}