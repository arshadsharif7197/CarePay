package com.carecloud.carepaylibray.signinsignup.dtos;

/**
 * Created by Rahul on 11/7/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SignInMetaDataDTO {

    @SerializedName("labels")
    @Expose
    private SignInLablesDTO labels;
    @SerializedName("links")
    @Expose
    private SignInLinksDTO links;
    @SerializedName("transitions")
    @Expose
    private SignInSignUpTransitionsDTO transitions;
    @SerializedName("data_models")
    @Expose
    private SignInDataModelDTO dataModels;

    /**
     * @return The labels
     */
    public SignInLablesDTO getLabels() {
        return labels;
    }

    /**
     * @param labels The labels
     */
    public void setLabels(SignInLablesDTO labels) {
        this.labels = labels;
    }

    /**
     * @return The links
     */
    public SignInLinksDTO getLinks() {
        return links;
    }

    /**
     * @param links The links
     */
    public void setLinks(SignInLinksDTO links) {
        this.links = links;
    }

    /**
     * @return The transitions
     */
    public SignInSignUpTransitionsDTO getTransitions() {
        return transitions;
    }

    /**
     * @param transitions The transitions
     */
    public void setTransitions(SignInSignUpTransitionsDTO transitions) {
        this.transitions = transitions;
    }

    /**
     * @return The dataModels
     */
    public SignInDataModelDTO getDataModels() {
        return dataModels;
    }

    /**
     * @param dataModels The data_models
     */
    public void setDataModels(SignInDataModelDTO dataModels) {
        this.dataModels = dataModels;
    }

}