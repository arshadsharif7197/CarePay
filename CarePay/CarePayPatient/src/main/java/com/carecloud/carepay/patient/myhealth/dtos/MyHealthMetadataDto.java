package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.appointments.models.LinksDTO;
import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthMetadataDto {

    @Expose
    private LinksDTO links = new LinksDTO();

    public LinksDTO getLinks() {
        return links;
    }

    public void setLinks(LinksDTO links) {
        this.links = links;
    }
}
