package com.carecloud.carepay.practice.library.homescreen.dtos;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class HomeScreenMetadataDTO {

    @SerializedName("links")
    @Expose
    private JsonObject links;
    @SerializedName("transitions")
    @Expose
    private JsonObject transitions;

    /**
     *
     * @return
     * The transitions
     */
    public JsonObject getTransitions() {
        return transitions;
    }

    /**
     *
     * @param transitions
     * The transitions
     */
    public void setTransitions(JsonObject transitions) {
        this.transitions = transitions;
    }

    public JsonObject getLinks() {
        return links;
    }

    public void setLinks(JsonObject links) {
        this.links = links;
    }
}
