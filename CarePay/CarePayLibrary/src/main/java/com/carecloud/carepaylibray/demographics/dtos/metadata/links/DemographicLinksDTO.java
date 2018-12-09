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
    private TransitionDTO self = new TransitionDTO();

    @SerializedName("demographics")
    @Expose
    private BaseLinkModel demographics = new BaseLinkModel();

    @SerializedName("search_physicians")
    @Expose
    private TransitionDTO searchPhysicians = new TransitionDTO();

    @SerializedName(value = "language_metadata", alternate = "language")
    @Expose
    private TransitionDTO language = new TransitionDTO();

    /**
     * @return The self
     */
    public TransitionDTO getSelf() {
        return self;
    }

    /**
     * @param self The self
     */
    public void setSelf(TransitionDTO self) {
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

    public TransitionDTO getSearchPhysicians() {
        return searchPhysicians;
    }

    public void setSearchPhysicians(TransitionDTO searchPhysicians) {
        this.searchPhysicians = searchPhysicians;
    }

    public TransitionDTO getLanguage() {
        return language;
    }

    public void setLanguage(TransitionDTO language) {
        this.language = language;
    }
}
