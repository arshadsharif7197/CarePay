package com.carecloud.carepaylibray.signinsignup.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 25/04/17.
 */

public class SignInMetaDataDTO {

    @SerializedName("labels")
    @Expose
    private SignInLabelsDTO labels = new SignInLabelsDTO();
    @SerializedName("links")
    @Expose
    private SignInLinksDTO links = new SignInLinksDTO();
    @SerializedName("transitions")
    @Expose
    private SignInTransitionsDTO transitions = new SignInTransitionsDTO();
    @SerializedName("data_models")
    @Expose
    private SignInDataModelDTO dataModels = new SignInDataModelDTO();

    public SignInLabelsDTO getLabels() {
        return labels;
    }

    public void setLabels(SignInLabelsDTO labels) {
        this.labels = labels;
    }

    public SignInLinksDTO getLinks() {
        return links;
    }

    public void setLinks(SignInLinksDTO links) {
        this.links = links;
    }

    public SignInTransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(SignInTransitionsDTO transitions) {
        this.transitions = transitions;
    }

    public SignInDataModelDTO getDataModels() {
        return dataModels;
    }

    public void setDataModels(SignInDataModelDTO dataModels) {
        this.dataModels = dataModels;
    }
}
