package com.carecloud.carepay.patient.retail.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailMetadataDTO {

    @SerializedName("links")
    private RetailLinksDTO links;

    @SerializedName("transitions")
    private RetailTransitionsDTO transitions;

    public RetailLinksDTO getLinks() {
        return links;
    }

    public void setLinks(RetailLinksDTO links) {
        this.links = links;
    }

    public RetailTransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(RetailTransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
