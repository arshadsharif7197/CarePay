package com.carecloud.carepay.patient.myhealth.dtos;

import com.google.gson.annotations.Expose;

/**
 * @author pjohnson on 18/08/17.
 */

public class EducationMaterial {

    @Expose
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
