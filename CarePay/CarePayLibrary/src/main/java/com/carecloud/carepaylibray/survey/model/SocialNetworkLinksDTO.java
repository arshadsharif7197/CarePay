package com.carecloud.carepaylibray.survey.model;

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
    @Expose
    @SerializedName("show_rate")
    private double linksRating;

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

    public double getLinksRating() {
        return linksRating;
    }

    public void setLinksRating(double linksRating) {
        this.linksRating = linksRating;
    }
}
