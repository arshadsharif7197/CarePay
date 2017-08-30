package com.carecloud.carepay.practice.library.practicesetting.models;

/**
 * Created by prem_mourya on 10/24/2016.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PracticeSettingMetadataDTO {
    @SerializedName("links")
    @Expose
    private PracticeSettingLinksDTO links = new PracticeSettingLinksDTO();

    /**
     *
     * @return
     *     The links
     */
    public PracticeSettingLinksDTO getLinks() {
        return links;
    }

    /**
     *
     * @param links
     *     The links
     */
    public void setLinks(PracticeSettingLinksDTO links) {
        this.links = links;
    }

}

