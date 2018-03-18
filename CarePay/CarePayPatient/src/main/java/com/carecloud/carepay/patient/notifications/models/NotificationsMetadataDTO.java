package com.carecloud.carepay.patient.notifications.models;

import com.carecloud.carepaylibray.appointments.models.TransitionsDTO;
import com.carecloud.carepaylibray.base.dtos.LandingLinks;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationsMetadataDTO {

    @SerializedName("links")
    @Expose
    private LandingLinks links = new LandingLinks();

    @SerializedName("transitions")
    @Expose
    private TransitionsDTO transitions = new TransitionsDTO();

    public LandingLinks getLinks() {
        return links;
    }

    public void setLinks(LandingLinks links) {
        this.links = links;
    }

    public TransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(TransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
