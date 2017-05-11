package com.carecloud.carepay.patient.notifications.models;

import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.carecloud.carepaylibray.appointments.models.TransitionsDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 5/11/17.
 */

public class NotificationsMetadataDTO {

    @SerializedName("links")
    @Expose
    private LinksDTO links = new LinksDTO();

    @SerializedName("transitions")
    @Expose
    private TransitionsDTO transitions = new TransitionsDTO();

    public LinksDTO getLinks() {
        return links;
    }

    public void setLinks(LinksDTO links) {
        this.links = links;
    }

    public TransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(TransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
