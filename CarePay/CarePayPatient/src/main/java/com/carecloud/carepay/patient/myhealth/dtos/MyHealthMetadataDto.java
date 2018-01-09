package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthMetadataDto {

    @Expose
    private MyHealthLinks links = new MyHealthLinks();

    @Expose
    private MyHealthTransitionsDto transitions = new MyHealthTransitionsDto();

    public MyHealthLinks getLinks() {
        return links;
    }

    public void setLinks(MyHealthLinks links) {
        this.links = links;
    }

    public MyHealthTransitionsDto getTransitions() {
        return transitions;
    }

    public void setTransitions(MyHealthTransitionsDto transitions) {
        this.transitions = transitions;
    }
}
