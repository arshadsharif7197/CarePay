package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepay.patient.patientsplash.dtos.TransitionsDTO;
import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthMetadataDto {

    @Expose
    private MyHealthLinks links = new MyHealthLinks();

    @Expose
    private TransitionsDTO transitions = new TransitionsDTO();

    public MyHealthLinks getLinks() {
        return links;
    }

    public void setLinks(MyHealthLinks links) {
        this.links = links;
    }

    public TransitionsDTO getTransitions() {
        return transitions;
    }

    public void setTransitions(TransitionsDTO transitions) {
        this.transitions = transitions;
    }
}
