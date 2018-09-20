package com.carecloud.carepay.patient.survey.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author pjohnson on 10/09/18.
 */
public class SocialNetworkLinksDTO {

    @Expose
    @SerializedName("enable")
    private boolean enable;
    @Expose
    @SerializedName("links")
    private List<SocialNetworkLink> links;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public List<SocialNetworkLink> getLinks() {
        return links;
    }

    public void setLinks(List<SocialNetworkLink> links) {
        this.links = links;
    }
}
