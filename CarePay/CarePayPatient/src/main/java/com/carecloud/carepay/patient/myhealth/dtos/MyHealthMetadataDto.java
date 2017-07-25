package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.patient.patientsplash.dtos.TransitionsDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthMetadataDto {

    @Expose
    private LinksDTO links = new LinksDTO();

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
