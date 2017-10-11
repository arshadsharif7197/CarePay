package com.carecloud.carepaylibray.demographics.dtos.metadata.links;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepaylibray.base.models.BaseLinkModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 9/19/2016.
 * Model for links.
 */
public class DemographicLinksDTO {
    @SerializedName("self")
    @Expose
    private BaseLinkModel self = new BaseLinkModel();

    @SerializedName("demographics")
    @Expose
    private BaseLinkModel demographics = new BaseLinkModel();

    @SerializedName("search_employers")
    @Expose
    private TransitionDTO searchEmployers = new TransitionDTO();

    /**
     * @return The self
     */
    public BaseLinkModel getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(BaseLinkModel self) {
        this.self = self;
    }

    /**
     * @return The demographics
     */
    public BaseLinkModel getDemographics() {
        return demographics;
    }

    /**
     * @param demographics The demographics
     */
    public void setDemographics(BaseLinkModel demographics) {
        this.demographics = demographics;
    }

    public TransitionDTO getSearchEmployers() {
        return searchEmployers;
    }

    public void setSearchEmployers(TransitionDTO searchEmployers) {
        this.searchEmployers = searchEmployers;
    }
}
