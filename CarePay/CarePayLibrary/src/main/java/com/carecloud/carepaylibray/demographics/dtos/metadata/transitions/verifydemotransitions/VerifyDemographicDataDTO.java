package com.carecloud.carepaylibray.demographics.dtos.metadata.transitions.verifydemotransitions;

/**
 * Created by Rahul on 10/31/16.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyDemographicDataDTO {

    @SerializedName("link")
    @Expose
    private String link;

    /**
     * @return The link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link The link
     */
    public void setLink(String link) {
        this.link = link;
    }

}